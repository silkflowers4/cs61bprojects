import org.junit.Test;
import static org.junit.Assert.*;
import java.util.List;
import java.util.Random;

/**
 * Test of a BST-based String Set.
 * @author Joshua Park
 */
public class ECHashStringSetTest  {
    // FIXME: Add your own tests for your ECHashStringSetTest

    @Test
    public void testSimplePut() {
        ECHashStringSet ecHashStringSet = initSimpleList();
        assertTrue(ecHashStringSet.contains("C"));
        assertTrue(ecHashStringSet.contains("D"));
    }

    private ECHashStringSet initSimpleList() {
        ECHashStringSet ecHashStringSet = new ECHashStringSet();
        ecHashStringSet.put("A");
        ecHashStringSet.put("B");
        ecHashStringSet.put("C");
        ecHashStringSet.put("D");
        ecHashStringSet.put("E");
        ecHashStringSet.put("F");
        return ecHashStringSet;
    }

    @Test
    public void testAdvancedPut() {
        ECHashStringSet ecHashStringSet = new ECHashStringSet();
        String alphabet = "abcdefghijklmnopqrstuvwxyz";
        Random random = new Random();

        for (int i = 0; i < 200; i++) {
            StringBuilder sb = new StringBuilder();
            for (int j = 0; j < 5; j++) {
                int index = random.nextInt(alphabet.length());
                sb.append(alphabet.charAt(index));
            }
            ecHashStringSet.put(sb.toString());
        }
    }
}
