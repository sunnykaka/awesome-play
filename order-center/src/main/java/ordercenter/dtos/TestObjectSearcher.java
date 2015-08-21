package ordercenter.dtos;

import ordercenter.constants.TestObjectItemStatus;
import ordercenter.constants.TestObjectStatus;
import org.joda.time.DateTime;

/**
 * Created by liubin on 15-4-3.
 */
public class TestObjectSearcher {

    public Integer orderNo;

    public TestObjectStatus status;

    public DateTime createTimeStart;

    public DateTime createTimeEnd;

    public TestObjectItemStatus testObjectItemStatus;

    public String productSku;

}
