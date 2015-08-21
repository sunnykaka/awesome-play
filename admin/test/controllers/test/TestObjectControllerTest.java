package controllers.test;

import base.PrepareTestObject;
import common.utils.JsonUtils;
import ordercenter.models.TestObject;
import ordercenter.models.TestObjectItem;
import org.junit.Test;
import play.mvc.Http;
import play.mvc.Result;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static play.test.Helpers.*;


/**
 * Created by liubin on 15-4-2.
 */
public class TestObjectControllerTest extends PrepareTestObject {

    @Test
    public void testCreateOrderByJson() throws Exception {

        TestObject testObject = initTestObject(3);

        Http.RequestBuilder request = new Http.RequestBuilder().method(POST).
                uri(controllers.test.routes.TestObjectController.createByJson().url()).
                bodyJson(JsonUtils.object2Node(testObject));

        Result result = route(request);
        assertThat(result.status(), is(OK));
        assertThat(result.contentType(), is("application/json"));
        assertThat(contentAsString(result), is("{\"result\":\"ok\"}"));

    }

    @Test
    public void testViewOrderByJson() throws Exception {

        prepareTestObjects(1, 3);

        doInTransactionWithGeneralDao(generalDao -> {

            List<TestObject> results = generalDao.query("select o from TestObject o", Optional.empty(), new HashMap<>());
            TestObject testObject = results.get(0);

            Http.RequestBuilder request = new Http.RequestBuilder().method(GET).
                    uri(controllers.test.routes.TestObjectController.viewByJson(testObject.getId()).url());

            Result result = route(request);
            System.out.println(contentAsString(result));
            assertThat(result.status(), is(OK));
            assertThat(result.contentType(), is("application/json"));
            TestObject testObjectAfter = JsonUtils.json2Object(contentAsString(result), TestObject.class);

            assertThat(testObjectAfter.getId(), is(testObject.getId()));
            assertThat(testObjectAfter.getOrderNo(), is(testObject.getOrderNo()));
            assertThat(testObjectAfter.getPlatformType(), is(testObject.getPlatformType()));
            assertThat(testObjectAfter.getStatus(), is(testObject.getStatus()));
            assertThat(testObjectAfter.getCreateTime(), is(testObject.getCreateTime()));
            assertThat(testObjectAfter.getActualFee(), is(testObject.getActualFee()));

            assertThat(testObjectAfter.getTestObjectItemList().size(), is(3));
            for(int i=0; i<testObject.getTestObjectItemList().size(); i++) {
                TestObjectItem testObjectItem = testObject.getTestObjectItemList().get(i);
                TestObjectItem testObjectItemAfter = testObjectAfter.getTestObjectItemList().get(i);

                assertThat(testObjectItemAfter.getStatus(), is(testObjectItem.getStatus()));
                assertThat(testObjectItemAfter.getProductSku(), is(testObjectItem.getProductSku()));
            }

            return null;

        });

    }





}
