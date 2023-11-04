package enigma;

import java.util.HashMap;


/** An alphabet of encodable characters.  Provides a mapping from characters
 *  to and from indices into the alphabet.
 *  @author Joshua Park
 */
class Alphabet {

    /** A new alphabet containing CHARS. The K-th character has index
     *  K (numbering from 0). No character may be duplicated.
     *  @author Joshua Park*/
    Alphabet(String chars) {
        for (int i = 0; i < chars.length(); i++) {
            if (!_alphabet.containsValue(chars.charAt(i))) {
                _alphabet.put(i, chars.charAt(i));
            }
        }
    }

    /** A default alphabet of all upper-case characters. */
    Alphabet() {
        this("ABCDEFGHIJKLMNOPQRSTUVWXYZ");
    }

    /** Returns the size of the alphabet. */
    int size() {
        return _alphabet.size();
    }

    /** Returns true if CH is in this alphabet. */
    boolean contains(char ch) {
        return _alphabet.containsValue(ch);
    }

    /** Returns character number INDEX in the alphabet, where
     *  0 <= INDEX < size(). */
    char toChar(int index) {
        char temp = '\0';
        if (index >= _alphabet.size() || index < 0) {
            throw new IndexOutOfBoundsException();
        }
        return _alphabet.get(index);
    }

    /** Returns the index of character CH which must be in
     *  the alphabet. This is the inverse of toChar(). */
    int toInt(char ch) {
        for (int i = 0; i < _alphabet.size(); i++) {
            if (_alphabet.get(i).equals(ch)) {
                return i;
            }
        }
        throw new EnigmaException("Character is not in the alphabet");
    }

    /**
     * Hashmap used for alphabet.
     */
    private HashMap<Integer, Character> _alphabet = new HashMap<>();

}
