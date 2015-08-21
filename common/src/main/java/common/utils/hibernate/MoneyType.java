package common.utils.hibernate;

import common.utils.Money;
import org.hibernate.HibernateException;
import org.hibernate.annotations.TypeDef;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.usertype.UserType;

import javax.persistence.MappedSuperclass;
import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

/**
 *
 */
@TypeDef(defaultForType = Money.class, typeClass = MoneyType.class)
public class MoneyType implements UserType {

    @Override
    public int[] sqlTypes() {
        return new int[]{Types.BIGINT};
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Class returnedClass() {
        return Money.class;
    }

    @Override
    public boolean equals(Object x, Object y) throws HibernateException {
        return x == y || !(x == null || y == null) && x.equals(y);
    }

    @Override
    public int hashCode(Object x) throws HibernateException {
        return x.hashCode();
    }

    @Override
    public Object nullSafeGet(ResultSet rs, String[] names, SessionImplementor session, Object owner)
            throws HibernateException, SQLException {
        Long value = rs.getLong(names[0]);
        if (rs.wasNull()) {
            return null;
        }
        return Money.valueOfCent(value);
    }

    @Override
    public void nullSafeSet(PreparedStatement st, Object value, int index, SessionImplementor session)
            throws HibernateException, SQLException {
        if (value == null) {
            st.setNull(index, Types.BIGINT);
        } else {
            Money date = (Money) value;
            st.setLong(index, date.getCent());
        }
    }

    @Override
    public Object deepCopy(Object value) throws HibernateException {
        return value;
    }

    @Override
    public boolean isMutable() {
        return false;
    }

    @Override
    public Serializable disassemble(Object value) throws HibernateException {
        return (Serializable) value;
    }

    @Override
    public Object assemble(Serializable cached, Object owner)
            throws HibernateException {
        return cached;
    }

    @Override
    public Object replace(Object original, Object target, Object owner)
            throws HibernateException {
        return original;
    }
}