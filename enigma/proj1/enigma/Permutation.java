package enigma;
import java.util.HashMap;
import java.util.HashSet;

/** Represents a permutation of a range of integers starting at 0 corresponding
 *  to the characters of an alphabet.
 *  @author Joshua Park
 */
class Permutation {

    /** Set this Permutation to that specified by CYCLES, a string in the
     *  form "(cccc) (cc) ..." where the c's are characters in ALPHABET, which
     *  is interpreted as a permutation in cycle notation.  Characters in the
     *  alphabet that are not included in any cycle map to themselves.
     *  Whitespace is ignored. */
    Permutation(String cycles, Alphabet alphabet) {
        _alphabet = alphabet;

        String replaced = cycles.replaceAll("[()]", "");
        String errorCheckString = replaced.replace(" ", "");
        HashSet<Character> checkForRepeats = new HashSet<>();
        for (int i = 0; i < errorCheckString.length(); i++) {
            char ch = errorCheckString.charAt(i);
            if (!checkForRepeats.contains(ch) && _alphabet.contains(ch)) {
                checkForRepeats.add(errorCheckString.charAt(i));
            } else {
                throw new EnigmaException("Repeat char orchar is invalid");
            }
        }

        _cycles = replaced.split(" ");
    }

    /** Add the cycle c0->c1->...->cm->c0 to the permutation, where CYCLE is
     *  c0c1...cm. */
    void addCycle(String cycle) {
        for (int j = 0; j < cycle.length(); j++) {
            _hash.put(cycle.charAt(j), cycle.charAt((j + 1) % cycle.length()));
        }
    }

    /** Return the value of P modulo the size of this permutation. */
    final int wrap(int p) {
        int r = p % size();
        if (r < 0) {
            r += size();
        }
        return r;
    }

    /** Returns the size of the alphabet I permute. */
    int size() {
        return _alphabet.size();
    }

    /** Return the result of applying this permutation to P modulo the
     *  alphabet size. */
    int permute(int p) {
        for (int cycleIndex = 0; cycleIndex < _cycles.length; cycleIndex++) {
            String cycle = _cycles[cycleIndex];
            for (int charIndex = 0; charIndex < cycle.length(); charIndex++) {
                if (cycle.charAt(charIndex) == _alphabet.toChar(wrap(p))) {
                    if (charIndex == cycle.length() - 1) {
                        char firstChar = cycle.charAt(0);
                        return _alphabet.toInt(firstChar);
                    } else {
                        char nextChar = cycle.charAt(charIndex + 1);
                        return _alphabet.toInt(nextChar);
                    }
                }
            }
        }
        return p;
    }

    /** Return the result of applying the inverse of this permutation
     *  to  C modulo the alphabet size. */
    int invert(int c) {
        for (int cycleIndex = 0; cycleIndex < _cycles.length; cycleIndex++) {
            String cycle = _cycles[cycleIndex];
            for (int charIndex = 0; charIndex < cycle.length(); charIndex++) {
                if (cycle.charAt(charIndex) == _alphabet.toChar(wrap(c))) {
                    if (charIndex == 0) {
                        char lastChar = cycle.charAt(cycle.length() - 1);
                        return _alphabet.toInt(lastChar);
                    } else {
                        char nextChar = cycle.charAt(charIndex - 1);
                        return _alphabet.toInt(nextChar);
                    }
                }
            }
        }
        return c;
    }

    /** Return the result of applying this permutation to the index of P
     *  in ALPHABET, and converting the result to a character of ALPHABET. */
    char permute(char p) {
        return _alphabet.toChar(permute(_alphabet.toInt(p)));
    }

    /** Return the result of applying the inverse of this permutation to C. */
    char invert(char c) {

        return _alphabet.toChar(invert(_alphabet.toInt(c)));
    }

    /** Return the alphabet used to initialize this Permutation. */
    Alphabet alphabet() {
        return _alphabet;
    }

    /** Return true iff this permutation is a derangement (i.e., a
     *  permutation for which no value maps to itself). */
    boolean derangement() {
        for (String cycle : _cycles) {
            if (cycle.length() == 1) {
                return false;
            }
        }
        return true;
    }

    /** Alphabet of this permutation. */
    private Alphabet _alphabet;

    /**
     * List of Cycles in string format.
     */
    private String[] _cycles;

    /**
     * Used for addCycle.
     */
    private HashMap<Character, Character> _hash = new HashMap<>();

}
