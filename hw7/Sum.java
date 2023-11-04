import java.util.Arrays;
/** HW #7, Two-sum problem.
 * @author
 */
public class Sum {

    /** Returns true iff A[i]+B[j] = M for some i and j. */
    public static boolean sumsTo(int[] A, int[] B, int m) {
        Arrays.sort(A);
        Arrays.sort(B);
        int index1 = 0;
        int index2 = B.length - 1;
        if (A.length == 0 && B.length == 0) {
            return false;
        }

        boolean sumsTo = false;
        while (index1 < index2 && !(A.length == 0 || B.length == 0)) {
            if (A[index1] + B[index2] == m) {
                sumsTo = true;
                break;
            } else if (A[index1] + B[index2] >= m) {
                // we are too large, index2 should go to the left
                index2--;
            } else {
                // we are too small, index1 should go to the right
                index1++;
            }
        }

        if (A.length == 0 && B.length != 0) {
            return sumsTo(B, B, m);
        }
        if (B.length == 0 && A.length != 0) {
            return sumsTo(A, A, m);
        }

        return sumsTo;
    }

//    public static void main(String[] args) {
//        int[] a = {};
//        int[] b = {1, 2, 4};
//        boolean sol = sumsTo(b, a, 10);
//        System.out.println(sol);
//    }

}
