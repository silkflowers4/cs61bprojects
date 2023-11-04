import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Note that every sorting algorithm takes in an argument k. The sorting
 * algorithm should sort the array from index 0 to k. This argument could
 * be useful for some of your sorts.
 *
 * Class containing all the sorting algorithms from 61B to date.
 *
 * You may add any number instance variables and instance methods
 * to your Sorting Algorithm classes.
 *
 * You may also override the empty no-argument constructor, but please
 * only use the no-argument constructor for each of the Sorting
 * Algorithms, as that is what will be used for testing.
 *
 * Feel free to use any resources out there to write each sort,
 * including existing implementations on the web or from DSIJ.
 *
 * All implementations except Counting Sort adopted from Algorithms,
 * a textbook by Kevin Wayne and Bob Sedgewick. Their code does not
 * obey our style conventions.
 */
public class MySortingAlgorithms {

    /**
     * Java's Sorting Algorithm. Java uses Quicksort for ints.
     */
    public static class JavaSort implements SortingAlgorithm {
        @Override
        public void sort(int[] array, int k) {
            Arrays.sort(array, 0, k);
        }

        @Override
        public String toString() {
            return "Built-In Sort (uses quicksort for ints)";
        }
    }

    /** Insertion sorts the provided data. */
    public static class InsertionSort implements SortingAlgorithm {
        @Override
        public void sort(int[] array, int k) {
            // FIXME
            for (int i = 1; i < k; i++) {
                // iterate backwards from index
                for (int j = i; j > 0; j--) {
                    if ( array[j] < array[j - 1]) {
                        swap(array, j, j - 1);
                    }
                }
            }
        }

        @Override
        public String toString() {
            return "Insertion Sort";
        }
    }

    /**
     * Selection Sort for small K should be more efficient
     * than for larger K. You do not need to use a heap,
     * though if you want an extra challenge, feel free to
     * implement a heap based selection sort (i.e. heapsort).
     */
    public static class SelectionSort implements SortingAlgorithm {
        @Override
        public void sort(int[] array, int k) {
            // FIXME
            int minIndex;
            for (int i = 0; i < k; i++) {
                minIndex = i;
                for (int j = i + 1; j < k; j++) {
                    if (array[j] < array[minIndex]) {
                        minIndex = j;
                    }
                }
                swap(array, i, minIndex);
            }
        }

        @Override
        public String toString() {
            return "Selection Sort";
        }
    }

    /** Your mergesort implementation. An iterative merge
     * method is easier to write than a recursive merge method.
     * Note: I'm only talking about the merge operation here,
     * not the entire algorithm, which is easier to do recursively.
     */
    public static class MergeSort implements SortingAlgorithm {
        @Override
        public void sort(int[] array, int k) {
            if (k == 1) {
                return;
            }
            int[] left = new int[(int) k / 2];
            int[] right = new int[k - (int) k / 2];

            if ((int) k / 2 >= 0) {
                System.arraycopy(array, 0, left, 0, (int) k / 2);
            }

            if (k - (int) k / 2 >= 0) {
                System.arraycopy(array, (int) k / 2, right, 0, k - (int) k / 2);
            }
            sort(left, (int) k / 2);
            sort(right, k - (int) k / 2);
            merge(array, left, right, (int) k / 2, k - (int) k / 2);
        }

        // may want to add additional methods
        static void merge(int[] array, int[] leftArray, int[] rightArray, int leftLen, int rightLen) {
            int leftIndex = 0;
            int rightIndex = 0;
            int arrayIndex = 0;
            // while left and right have elements
            while (leftIndex < leftLen && rightIndex < rightLen) {
                if (leftArray[leftIndex] > rightArray[rightIndex]) {
                    // advance right
                    array[arrayIndex] = rightArray[rightIndex];
                    rightIndex++;
                }
                else {
                    // advance left
                    array[arrayIndex] = leftArray[leftIndex];
                    leftIndex++;
                }
                arrayIndex++;
            }

            // while left has elements
            while (leftIndex < leftLen) {
                array[arrayIndex] = leftArray[leftIndex];;
                leftIndex++;
                arrayIndex++;
            }

            // while right has elements
            while (rightIndex < rightLen) {
                array[arrayIndex] = rightArray[rightIndex];
                rightIndex++;
                arrayIndex++;
            }
        }
        @Override
        public String toString() {
            return "Merge Sort";
        }
    }

    /**
     * Your Counting Sort implementation.
     * You should create a count array that is the
     * same size as the value of the max digit in the array.
     */
    public static class CountingSort implements SortingAlgorithm {
        @Override
        public void sort(int[] array, int k) {
            // FIXME: to be implemented
        }

        // may want to add additional methods

        @Override
        public String toString() {
            return "Counting Sort";
        }
    }

    /** Your Heapsort implementation.
     */
    public static class HeapSort implements SortingAlgorithm {
        @Override
        public void sort(int[] array, int k) {
            // FIXME
        }

        @Override
        public String toString() {
            return "Heap Sort";
        }
    }

    /** Your Quicksort implementation.
     */
    public static class QuickSort implements SortingAlgorithm {
        @Override
        public void sort(int[] array, int k) {
            // FIXME
        }

        @Override
        public String toString() {
            return "Quicksort";
        }
    }

    /* For radix sorts, treat the integers as strings of x-bit numbers.  For
     * example, if you take x to be 2, then the least significant digit of
     * 25 (= 11001 in binary) would be 1 (01), the next least would be 2 (10)
     * and the third least would be 1.  The rest would be 0.  You can even take
     * x to be 1 and sort one bit at a time.  It might be interesting to see
     * how the times compare for various values of x. */

    /**
     * LSD Sort implementation.
     */
    public static class LSDSort implements SortingAlgorithm {
        @Override
        public void sort(int[] a, int k) {
            // FIXME
            int mostDigits = Integer.MIN_VALUE;
            for (int value : a) {
                int digits = (int) (Math.log10(value) + 1);
                mostDigits = Math.max(mostDigits, digits);
            }
            int place = 1;
            while (place <= mostDigits) {
                countSort(a, place, k);
                place++;
            }
        }
        @Override
        public String toString() {
            return "LSD Sort";
        }

        private void countSort(int[] a, int place, int k) {
            LinkedList<Integer>[] buckets = new LinkedList[10];
            for (int i = 0; i < 10; i++) {
                buckets[i] = new LinkedList<>();
            }
            for (int i = 0; i < k; i++) {
                buckets[(int) ((a[i] / Math.pow(10, place - 1)) % 10)].addLast(a[i]);
            }
            int i = 0;
            for (List<Integer> individual : buckets) {
                for (int integer : individual) {
                    a[i] = integer;
                    i++;
                }
            }
        }
    }

    /**
     * MSD Sort implementation.
     */
    public static class MSDSort implements SortingAlgorithm {
        @Override
        public void sort(int[] a, int k) {
            // FIXME
        }

        @Override
        public String toString() {
            return "MSD Sort";
        }
    }

    /** Exchange A[I] and A[J]. */
    private static void swap(int[] a, int i, int j) {
        int swap = a[i];
        a[i] = a[j];
        a[j] = swap;
    }

}
