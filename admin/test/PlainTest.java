import com.google.common.base.Functions;
import org.junit.Test;

import java.util.Arrays;

/**
 * Created by liubin on 2016/8/16.
 */
public class PlainTest {


    @Test
    public void test() {
        A[] arrayA = new A[]{new B(), new B()};
        B[] arrayB = (B[]) Arrays.stream(arrayA).map(f -> Functions.identity().apply(f)).toArray();
        System.out.println(arrayB);
    }

    interface A {}
    static class B implements A {}
    static class C implements A {}

}
