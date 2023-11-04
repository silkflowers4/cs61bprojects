import org.junit.Test;
import static org.junit.Assert.*;
import java.util.List;

/**
 * Test of a BST-based String Set.
 * @author
 */
public class BSTStringSetTest  {
    // FIXME: Add your own tests for your BST StringSet

    @Test
    public void testPut() {
        BSTStringSet bstStringSet = initList();
        assertTrue(bstStringSet.contains("C"));
        assertTrue(bstStringSet.contains("D"));
    }

    @Test
    public void testAsList() {
        BSTStringSet bstStringSet = initList();
        List<String> list = bstStringSet.asList();
        assertEquals("A", list.get(0));
        assertEquals("F", list.get(5));    }

    private BSTStringSet initList() {
        BSTStringSet bstStringSet = new BSTStringSet();
        bstStringSet.put("A");
        bstStringSet.put("B");
        bstStringSet.put("C");
        bstStringSet.put("D");
        bstStringSet.put("E");
        bstStringSet.put("F");
        return bstStringSet;
    }
}
