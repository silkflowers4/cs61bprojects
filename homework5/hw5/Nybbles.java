/** Represents an array of integers each in the range -8..7.
 *  Such integers may be represented in 4 bits (called nybbles).
 *  @author
 */
public class Nybbles {

    /** Maximum positive value of a Nybble. */
    public static final int MAX_VALUE = 7;

    /** Return an array of size N. 
    * DON'T CHANGE THIS.*/
    public Nybbles(int N) {
        _data = new int[(N + 7) / 8];
        _n = N;
    }

    /** Return the size of THIS. */
    public int size() {
        return _n;
    }

    /** Return the Kth integer in THIS array, numbering from 0.
     *  Assumes 0 <= K < N. */
    public int get(int k) {
        if (k < 0 || k >= _n) {
            throw new IndexOutOfBoundsException();
        } else {
            //int l = _data[k/8]<< ((7- (k%8)) * 4);
            //return l >> (7*4);// REPLACE WITH SOLUTION
            //return (_data[k/8]<< ((7- (k%8)) * 4))>> (7*4);

            int index = k/8;
//            int left = _data[index] << ((7 - (k%8)) *4);
            int left = _data[index] << ((7 * 4) - 4 * (k % 8));
            int right = left >> (28);
            return right;
        }
    }

    /** Set the Kth integer in THIS array to VAL.  Assumes
     *  0 <= K < N and -8 <= VAL < 8. */
    public void set(int k, int val) {
        if (k < 0 || k >= _n) {
            throw new IndexOutOfBoundsException();
        } else if (val < (-MAX_VALUE - 1) || val > MAX_VALUE) {
            throw new IllegalArgumentException();
        } else {
            int index = k/8;
            _data[index] &= ~(15 << (7 * 4) >> ((7 * 4) - (4 * ( k % 8))));
            val = val << (7 * 4) >> ((7 * 4) - 4 * (k % 8));
            _data[index] |= val;
        }
    }

    /** DON'T CHANGE OR ADD TO THESE.*/
    /** Size of current array (in nybbles). */
    private int _n;
    /** The array data, packed 8 nybbles to an int. */
    private int[] _data;
}
