package common.utils.play;

import com.google.common.collect.ImmutableList;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.NotReadablePropertyException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DataBinder;
import org.springframework.validation.FieldError;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;
import play.Logger;
import play.data.Form;
import play.data.validation.ValidationError;
import play.libs.F;

import javax.validation.ConstraintViolation;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

import static play.libs.F.None;
import static play.libs.F.Some;

/**
 * Created by liubin on 15/4/11.
 */
public class PlayForm<T> extends Form<T> {

    private java.lang.reflect.Field rootNameField = null;
    private java.lang.reflect.Field backedTypeField = null;
//    private java.lang.reflect.Field dataField = null;
    private java.lang.reflect.Field errorsField = null;
//    private java.lang.reflect.Field valueField = null;
    private java.lang.reflect.Field groupsField = null;
    private java.lang.reflect.Method blankInstanceMethod = null;


    /**
     * Creates a new <code>Form</code>.
     *
     * @param clazz wrapped class
     */
    public PlayForm(Class<T> clazz) {
        this(null, clazz);
    }

    @SuppressWarnings("unchecked")
    public PlayForm(String name, Class<T> clazz) {
        this(name, clazz, new HashMap<String,String>(), new HashMap<String,List<ValidationError>>(), None(),  null);
    }

    @SuppressWarnings("unchecked")
    public PlayForm(String name, Class<T> clazz, Class<?> groups) {
        this(name, clazz, new HashMap<String,String>(), new HashMap<String,List<ValidationError>>(), None(), groups);
    }

    public PlayForm(String rootName, Class<T> clazz, Map<String,String> data, Map<String,List<ValidationError>> errors, F.Option<T> value) {
        this(rootName, clazz, data, errors, value, null);
    }

    /**
     * Instantiates a new form that wraps the specified class.
     */
    public static <T> PlayForm<T> form(Class<T> clazz) {
        return new PlayForm<T>(clazz);
    }

    /**
     * Instantiates a new form that wraps the specified class.
     */
    public static <T> PlayForm<T> form(String name, Class<T> clazz) {
        return new PlayForm<T>(name, clazz);
    }

    /**
     * Instantiates a new form that wraps the specified class.
     */
    public static <T> PlayForm<T> form(String name, Class<T> clazz, Class<?> group) {
        return new PlayForm<T>(name, clazz, group);
    }

    /**
     * Instantiates a new form that wraps the specified class.
     */
    public static <T> PlayForm<T> form(Class<T> clazz, Class<?> group) {
        return new PlayForm<T>(null, clazz, group);
    }


    /**
     * Creates a new <code>Form</code>.
     *
     * @param clazz wrapped class
     * @param data the current form data (used to display the form)
     * @param errors the collection of errors associated with this form
     * @param value optional concrete value of type <code>T</code> if the form submission was successful
     */
    public PlayForm(String rootName, Class<T> clazz, Map<String,String> data, Map<String,List<ValidationError>> errors, F.Option<T> value, Class<?> groups) {

        super(rootName, clazz, data, errors, value, groups);

        try {
            rootNameField = Form.class.getDeclaredField("rootName");
            rootNameField.setAccessible(true);
            backedTypeField = Form.class.getDeclaredField("backedType");
            backedTypeField.setAccessible(true);
//            dataField = Form.class.getDeclaredField("data");
//            dataField.setAccessible(true);
            errorsField = Form.class.getDeclaredField("errors");
            errorsField.setAccessible(true);
//            valueField = Form.class.getDeclaredField("value");
//            valueField.setAccessible(true);
            groupsField = Form.class.getDeclaredField("groups");
            groupsField.setAccessible(true);
            blankInstanceMethod = Form.class.getDeclaredMethod("blankInstance");
            blankInstanceMethod.setAccessible(true);

        } catch (NoSuchFieldException | NoSuchMethodException e) {
            Logger.error("", e);
            throw new RuntimeException(e.getMessage());
        }

    }

    private T blankInstance() {
        try {
            return (T)blankInstanceMethod.invoke(this);
        } catch (IllegalAccessException | InvocationTargetException e) {
            Logger.error("", e);
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public Form<T> bind(Map<String, String> data, String... allowedFields) {

        String rootName = null;
        Class<?> groups = null;
        Class<T> backedType = null;
        Map<String,List<ValidationError>> errorsProperty = null;

        try {
            rootName = (String)rootNameField.get(this);
            groups = (Class<?>)groupsField.get(this);
            backedType = (Class<T>)backedTypeField.get(this);
            errorsProperty = (Map<String,List<ValidationError>>)errorsField.get(this);

        } catch (IllegalAccessException e) {
            Logger.error("", e);
            throw new RuntimeException(e.getMessage());
        }

        //copied from play.data.Form.bind
        //change "new Form(rootName, backedType, data, errors, None(), groups)" to
        //"new Form(rootName, backedType, data, errors, Some((T) result.getTarget()), groups);"
        //and "new Form(rootName, backedType, new HashMap<String, String>(data), new HashMap<String, List<ValidationError>>(errors), Some((T) result.getTarget()), groups);"
        //to "new Form(rootName, backedType, new HashMap<String, String>(data), new HashMap<String, List<ValidationError>>(errorsProperty), Some((T) result.getTarget()), groups);"

        DataBinder dataBinder = null;
        Map<String, String> objectData = data;
        if (rootName == null) {
            dataBinder = new DataBinder(blankInstance());
        } else {
            dataBinder = new DataBinder(blankInstance(), rootName);
            objectData = new HashMap<String, String>();
            for (String key : data.keySet()) {
                if (key.startsWith(rootName + ".")) {
                    objectData.put(key.substring(rootName.length() + 1), data.get(key));
                }
            }
        }
        if (allowedFields.length > 0) {
            dataBinder.setAllowedFields(allowedFields);
        }
        SpringValidatorAdapter validator = new SpringValidatorAdapter(play.data.validation.Validation.getValidator());
        dataBinder.setValidator(validator);
        dataBinder.setConversionService(play.data.format.Formatters.conversion);
        dataBinder.setAutoGrowNestedPaths(true);
        dataBinder.bind(new MutablePropertyValues(objectData));
        Set<ConstraintViolation<Object>> validationErrors;
        if (groups != null) {
            validationErrors = validator.validate(dataBinder.getTarget(), groups);
        } else {
            validationErrors = validator.validate(dataBinder.getTarget());
        }

        BindingResult result = dataBinder.getBindingResult();

        for (ConstraintViolation<Object> violation : validationErrors) {
            String field = violation.getPropertyPath().toString();
            FieldError fieldError = result.getFieldError(field);
            if (fieldError == null || !fieldError.isBindingFailure()) {
                try {
                    result.rejectValue(field,
                            violation.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName(),
                            getArgumentsForConstraint(result.getObjectName(), field, violation.getConstraintDescriptor()),
                            violation.getMessage());
                } catch (NotReadablePropertyException ex) {
                    throw new IllegalStateException("JSR-303 validated property '" + field +
                            "' does not have a corresponding accessor for data binding - " +
                            "check your DataBinder's configuration (bean property versus direct field access)", ex);
                }
            }
        }

        if (result.hasErrors()) {
            Map<String, List<ValidationError>> errors = new HashMap<String, List<ValidationError>>();
            for (FieldError error : result.getFieldErrors()) {
                String key = error.getObjectName() + "." + error.getField();
                if (key.startsWith("target.") && rootName == null) {
                    key = key.substring(7);
                }
                List<Object> arguments = new ArrayList<Object>();
                for (Object arg : error.getArguments()) {
                    if (!(arg instanceof org.springframework.context.support.DefaultMessageSourceResolvable)) {
                        arguments.add(arg);
                    }
                }
                if (!errors.containsKey(key)) {
                    errors.put(key, new ArrayList<ValidationError>());
                }

                ValidationError validationError = null;
                if (error.isBindingFailure()) {
                    ImmutableList.Builder<String> builder = ImmutableList.builder();
                    for (String code : error.getCodes()) {
                        builder.add(code.replace("typeMismatch", "error.invalid"));
                    }
                    validationError = new ValidationError(key, builder.build(), arguments);
                } else {
                    validationError = new ValidationError(key, error.getDefaultMessage(), arguments);
                }
                errors.get(key).add(validationError);
            }
            return new Form(rootName, backedType, data, errors, Some((T) result.getTarget()), groups);
        } else {
            Object globalError = null;
            if (result.getTarget() != null) {
                try {
                    java.lang.reflect.Method v = result.getTarget().getClass().getMethod("validate");
                    globalError = v.invoke(result.getTarget());
                } catch (NoSuchMethodException e) {
                } catch (Throwable e) {
                    throw new RuntimeException(e);
                }
            }
            if (globalError != null) {
                Map<String, List<ValidationError>> errors = new HashMap<String, List<ValidationError>>();
                if (globalError instanceof String) {
                    errors.put("", new ArrayList<ValidationError>());
                    errors.get("").add(new ValidationError("", (String) globalError, new ArrayList()));
                } else if (globalError instanceof List) {
                    for (ValidationError error : (List<ValidationError>) globalError) {
                        List<ValidationError> errorsForKey = errors.get(error.key());
                        if (errorsForKey == null) {
                            errors.put(error.key(), errorsForKey = new ArrayList<ValidationError>());
                        }
                        errorsForKey.add(error);
                    }
                } else if (globalError instanceof Map) {
                    errors = (Map<String, List<ValidationError>>) globalError;
                }
                return new Form(rootName, backedType, data, errors,  Some((T) result.getTarget()), groups);
            }

            return new Form(rootName, backedType, new HashMap<String, String>(data), new HashMap<String, List<ValidationError>>(errorsProperty), Some((T) result.getTarget()), groups);

        }
    }
}
