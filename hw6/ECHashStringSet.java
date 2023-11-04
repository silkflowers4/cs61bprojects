import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/** A set of String values.
 *  @author Joshua Park
 */
class ECHashStringSet implements StringSet {
    private LinkedList<String>[] buckets;
    private int defaultNumBuckets = 5;
    private int numItems = 0;
    private double maxLoadFactor = 5;
    private double minLoadFactor = 0.2;

    public ECHashStringSet() {
        buckets = (LinkedList<String>[]) new LinkedList[defaultNumBuckets];
        // loop and make sure buckets have linkedLists
        for (int i = 0; i < buckets.length; i++) {
            buckets[i] = new LinkedList<>();
        }
    }

    @Override
    public void put(String s) {
        // FIXME
        // check load limit and resize if needed
        // n_items / n_total_slots:
        if ((double) (numItems / buckets.length) > maxLoadFactor) {
            resize();
        }

        // add to correct bucket
        int hashcode = getStringHash(s);
        LinkedList<String> list = buckets[hashcode];
        if (!list.contains(s)) {
            list.addLast(s);
            numItems++;
        }
    }

    public void resize() {
        defaultNumBuckets = (int) (minLoadFactor * (numItems + 1)) + 1;
        List<String> items = this.asList();

        buckets = (LinkedList<String>[]) new LinkedList[defaultNumBuckets];
        // loop and make sure buckets have linkedLists
        for (int i = 0; i < buckets.length; i++) {
            buckets[i] = new LinkedList<>();
        }

        LinkedList<String> list;
        int hashcode;
        for (String s : items) {
            hashcode = getStringHash(s);
            list = buckets[hashcode];
            list.addLast(s);
        }
    }

    @Override
    public boolean contains(String s) {
        // FIXME
        int hashcode = getStringHash(s);
        LinkedList<String> list = buckets[hashcode];
        return list.contains(s);
    }

    @Override
    public List<String> asList() {
        // FIXME
        List<String> bucketsToList = new ArrayList<>();
        for (LinkedList<String> list : buckets) {
            bucketsToList.addAll(list);
        }
        return bucketsToList;
    }

    private int getStringHash(String s) {
        int bucketCount = buckets.length;
        return (s.hashCode() & 0x7fffffff) % bucketCount;
    }
}
