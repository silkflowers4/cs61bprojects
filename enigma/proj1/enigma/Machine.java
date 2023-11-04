package enigma;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


/** Class that represents a complete enigma machine.
 *  @author Joshua Park
 */
class Machine {

    /** A new Enigma machine with alphabet ALPHA, 1 < NUMROTORS rotor slots,
     *  and 0 <= PAWLS < NUMROTORS pawls.  ALLROTORS contains all the
     *  available rotors. */
    Machine(Alphabet alpha, int numRotors, int pawls,
            Collection<Rotor> allRotors) {
        _alphabet = alpha;
        _numRotors = numRotors;
        _pawls = pawls;
        _allRotors = allRotors;
        _rotorList = new Rotor[numRotors];

        _rotorAdvance = new boolean[_numRotors];
        for (int i = 0; i < numRotors; i++) {
            _rotorAdvance[i] = false;
        }

        int index = 0;
        for (Rotor rotor : allRotors) {
            _rotorNames.add(index, rotor.name());
            index += 1;
        }
    }

    /** Return the number of rotor slots I have. */
    int numRotors() {
        return _numRotors;
    }

    /** Return the number pawls (and thus rotating rotors) I have. */
    int numPawls() {
        return _pawls;
    }

    /** Set my rotor slots to the rotors named ROTORS from my set of
     *  available rotors (ROTORS[0] names the reflector).
     *  Initially, all rotors are set at their 0 setting. */
    void insertRotors(String[] rotors) {
        _rotors = new ArrayList<>();

        for (String rotorName : rotors) {
            if (!_rotorNames.contains(rotorName)) {
                throw new EnigmaException("rotor missing");
            }
        }

        int index = 0;
        for (String rotorName : rotors) {
            for (Rotor rotor: _allRotors) {
                if (rotorName.equals(rotor.name())) {
                    _rotorList[index] = rotor;
                }
            }
            index++;
        }
    }

    /** Set my rotors according to SETTING, which must be a string of
     *  numRotors()-1 characters in my alphabet. The first letter refers
     *  to the leftmost rotor setting (not counting the reflector).  */
    void setRotors(String setting) {
        for (int i = 1; i < numRotors(); i++) {
            _rotorList[i].set(setting.charAt(i - 1));
        }
    }

    /** Set the plugboard to PLUGBOARD. */
    void setPlugboard(Permutation plugboard) {
        _plugboard = plugboard;
    }

    /** Returns the result of converting the input character C (as an
     *  index in the range 0..alphabet size - 1), after first advancing
     *  the machine. */
    int convert(int c) {
        int convertNum = c;
        if (_plugboard != null) {
            convertNum = _plugboard.permute(c);
        }
        for (int i = _rotorList.length - 1; i > -1; i--) {
            if (_rotorList[i].atNotch()) {
                if (_rotorList[ i - 1].rotates()) {
                    setAdvancingRotors(i);
                    setAdvancingRotors(i - 1);
                }
            }
        }
        int index = 0;
        _rotorAdvance[_rotorList.length - 1] = true;
        for (boolean canAdvance : _rotorAdvance) {
            Rotor rotor = _rotorList[index];
            if (canAdvance) {
                rotor.advance();
                _rotorAdvance[index] = false;
            }
            index += 1;
        }
        for (int i = _rotorList.length - 1; i > -1; i--) {
            convertNum = _rotorList[i].convertForward(convertNum);
        }
        for (int i = 1; i < _rotorList.length; i++) {
            convertNum = _rotorList[i].convertBackward(convertNum);
        }
        if (_plugboard != null) {
            convertNum = _plugboard.invert(convertNum);
        }
        c = convertNum;
        return c;
    }

    /** Returns the encoding/decoding of MSG, updating the state of
     *  the rotors accordingly. */
    String convert(String msg) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < msg.length(); i++) {
            if (Character.isWhitespace(msg.charAt(i))) {
                sb.append(msg.charAt(i));
            } else {
                int index = _alphabet.toInt(msg.charAt(i));
                int convertIndex = convert(index);
                sb.append(_alphabet.toChar(convertIndex));
            }
        }
        return sb.toString();
    }

    /**
     * Set a specific rotor in array to be able to advance.
     * @param index index in rotorAdvance Array
     */
    void setAdvancingRotors(int index) {
        _rotorAdvance[index] = true;
    }

    /** Common alphabet of my rotors. */
    /**
     * Alphabet variable.
     */
    private final Alphabet _alphabet;
    /**
     * Number of rotors in machine.
     */
    private int _numRotors;
    /**
     * number of pawls.
     */
    private int _pawls;
    /**
     * Plugboard permutation.
     */
    private Permutation _plugboard;
    /**
     * Provided collection of rotors.
     */
    private Collection<Rotor> _allRotors;
    /**
     * ArrayList of rotors.
     */
    private ArrayList<Rotor> _rotors;

    /**
     * List for rotor Names.
     */
    private List<String> _rotorNames = new ArrayList<>();
    /**
     * boolean array for if rotor Advances.
     */
    private boolean[] _rotorAdvance;
    /**
     * RotorArray of rotors, for in order.
     */
    private Rotor[] _rotorList;
}
