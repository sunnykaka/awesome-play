package controllers.test;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.base.Joiner;
import common.utils.IdUtils;
import common.utils.JsonUtils;
import common.utils.page.PageFactory;
import common.utils.play.PlayForm;
import ordercenter.constants.TestObjectStatus;
import ordercenter.models.TestObject;
import ordercenter.models.TestObjectItem;
import ordercenter.services.TestObjectService;
import org.springframework.beans.factory.annotation.Autowired;
import play.data.Form;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.test.add;
import views.html.test.list;
import views.html.test.update;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Optional.*;

@org.springframework.stereotype.Controller
public class TestObjectController extends Controller {

    @Autowired
    private TestObjectService testObjectService;


    public Result list(String status, String orderNo) {

        List<TestObject> testObjectList = testObjectService.findByKey(of(PageFactory.getPage(request())), ofNullable(orderNo),
                ofNullable(status).map(TestObjectStatus::valueOf), empty(), empty());

        return ok(list.render(testObjectList));
    }

    public Result addPage() {
        return ok(add.render(Form.form(TestObject.class)));
    }

    public Result updatePage(Integer id) {
        return ok(update.render(Form.form(TestObject.class).fill(testObjectService.get(id))));
    }

    public Result saveTestObject() {

        Form<TestObject> form = PlayForm.form(TestObject.class).bindFromRequest();
        TestObject testObject = form.get();
        if(form.hasErrors()) {
            if(form.globalError() != null) {
                System.out.println("global error: " + form.globalError().message());
            }

            form.errors().forEach((k, v) -> System.out.println(
                    String.format("error key: %s, error value: %s", k,
                            Joiner.on("").join(v.stream().map(x -> x.toString()).collect(Collectors.toList())))));

            System.out.println(testObject == null);
            System.out.println(testObject.getBuyerId());

            return ok(add.render(form));
        } else {

            System.out.println(testObject.getBuyerId());
            System.out.println(testObject.getBuyTime());
            System.out.println(testObject.getStatus());
            System.out.println(testObject.getActualFee());
            if(!testObject.getTestObjectItemList().isEmpty()) {
                testObject.getTestObjectItemList().forEach(oi -> {
                    System.out.println(oi.getProductId());
                    System.out.println(oi.getProductSku());
                });
            }


            if(IdUtils.isEmpty(testObject.getId())) {

                testObjectService.saveTestObject(testObject);

            } else {

                //拷贝修改的数据
                TestObject testObjectInDb = testObjectService.get(testObject.getId());
                testObjectInDb.setActualFee(testObject.getActualFee());
                testObjectInDb.setBuyerId(testObject.getBuyerId());
                testObjectInDb.setBuyTime(testObject.getBuyTime());
                testObjectInDb.setStatus(testObject.getStatus());

                for(TestObjectItem oi : testObject.getTestObjectItemList()) {
                    for(TestObjectItem oiInDb : testObjectInDb.getTestObjectItemList()) {
                        if(oi.getId().equals(oiInDb.getId())) {
                            oiInDb.setProductSku(oi.getProductSku());
                            oiInDb.setProductId(oi.getProductId());
                        }
                    }
                }

                testObjectService.saveTestObject(testObjectInDb);
            }


            return redirect(routes.TestObjectController.list(null, null));
        }
    }

    @BodyParser.Of(BodyParser.Json.class)
    public Result createByJson() throws IOException {

        JsonNode jsonNode = request().body().asJson();
        System.out.println(jsonNode == null ? "null json" : ("json request: " + jsonNode.toString()));
        TestObject testObject = JsonUtils.node2Object(jsonNode, TestObject.class);

        System.out.println(testObject.getBuyTime());
        System.out.println(testObject.getStatus());
        System.out.println(testObject.getActualFee());
        if(!testObject.getTestObjectItemList().isEmpty()) {
            testObject.getTestObjectItemList().forEach(oi -> {
                System.out.println(oi.getProductId());
                System.out.println(oi.getProductSku());
            });
        }

        HashMap<String, String> result = new HashMap<>();
        result.put("result", "ok");
        return ok(JsonUtils.object2Node(result));
    }

    public Result viewByJson(Integer testObjectId) throws JsonProcessingException {

        TestObject testObject = testObjectService.get(testObjectId);

        return ok(JsonUtils.object2Node(testObject));

    }

}