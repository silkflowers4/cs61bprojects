package enigma;

import static enigma.EnigmaException.*;

/** Class that represents a rotating rotor in the enigma machine.
 *  @author Joshua Park
 */
class MovingRotor extends Rotor {

    /** A rotor named NAME whose permutation in its default setting is
     *  PERM, and whose notches are at the positions indicated in NOTCHES.
     *  The Rotor is initally in its 0 setting (first character of its
     *  alphabet).
     */
    MovingRotor(String name, Permutation perm, String notches) {
        super(name, perm);
        this._notches = notches;


    }



    @Override
    void advance() {

        set(permutation().wrap(super.setting() + 1));
    }

    /**
     *
     * @return returns true for moving rotor.
     */
    boolean rotates() {
        return true;
    }

    /**
     *
     * @return returns true if it is at a notch.
     */
    boolean atNotch() {
        for (int i = 0; i < _notches.length(); i++) {
            if (setting() != alphabet().toInt(_notches.charAt(i))) {
                continue;
            } else {
                return true;
            }
        }
        return false;
    }

    /**
     * String of notches used in rotor.
     */
    private String _notches;

}
