package enigma;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Scanner;

import static enigma.EnigmaException.*;

/**
 * Enigma simulator.
 *
 * @author Joshua Park
 */
public final class Main {

    /**
     * Process a sequence of encryptions and decryptions, as
     * specified by ARGS, where 1 <= ARGS.length <= 3.
     * ARGS[0] is the name of a configuration file.
     * ARGS[1] is optional; when present, it names an input file
     * containing messages.  Otherwise, input comes from the standard
     * input.  ARGS[2] is optional; when present, it names an output
     * file for processed messages.  Otherwise, output goes to the
     * standard output. Exits normally if there are no errors in the input;
     * otherwise with code 1.
     */
    public static void main(String... args) {
        try {
            new Main(args).process();
            return;
        } catch (EnigmaException excp) {
            System.err.printf("Error: %s%n", excp.getMessage());
        }
        System.exit(1);
    }

    /**
     * Check ARGS and open the necessary files (see comment on main).
     */
    Main(String[] args) {
        if (args.length < 1 || args.length > 3) {
            throw error("Only 1, 2, or 3 command-line arguments allowed");
        }

        _config = getInput(args[0]);

        if (args.length > 1) {
            _input = getInput(args[1]);
        } else {
            _input = new Scanner(System.in);
        }

        if (args.length > 2) {
            _output = getOutput(args[2]);
        } else {
            _output = System.out;
        }
    }

    /**
     * Return a Scanner reading from the file named NAME.
     */
    private Scanner getInput(String name) {
        try {
            return new Scanner(new File(name));
        } catch (IOException excp) {
            throw error("could not open %s", name);
        }
    }

    /**
     * Return a PrintStream writing to the file named NAME.
     */
    private PrintStream getOutput(String name) {
        try {
            return new PrintStream(new File(name));
        } catch (IOException excp) {
            throw error("could not open %s", name);
        }
    }

    /**
     * Configure an Enigma machine from the contents of configuration
     * file _config and apply it to the messages in _input, sending the
     * results to _output.
     */
    private void process() {
        Machine newMachine = readConfig();
        boolean firstConfigLine = true;
        String line;
        while (_input.hasNextLine()) {
            line = _input.nextLine().trim();
            if (firstConfigLine && line.charAt(0) != '*') {
                throw new EnigmaException("No asterisk or at wrong spot");
            }
            if (line.length() > 0 && line.charAt(0) == '*') {
                setUp(newMachine, line);
                firstConfigLine = false;
            } else if (line.trim().isBlank()) {
                _output.println();
            } else {
                printMessageLine(newMachine.convert(line));
                if (_input.hasNextLine()) {
                    _output.println();
                }
            }
        }
    }

    /**
     * Return an Enigma machine configured from the contents of configuration
     * file _config.
     */
    private Machine readConfig() {
        try {
            int numRotors = 0;
            int numPawls = 0;
            ArrayList<Rotor> allRotors = new ArrayList<>();

            if (_config.hasNextLine()) {
                String alphabet = _config.nextLine();
                _alphabet = new Alphabet(alphabet);
            } else {
                throw new EnigmaException("Alphabet not provided");
            }

            if (_config.hasNextLine()) {
                if (_config.hasNextInt()) {
                    numRotors = _config.nextInt();
                }
                if (_config.hasNextInt()) {
                    numPawls = _config.nextInt();
                }
            } else {
                throw new EnigmaException("Num rotors or pawls not provided");
            }

            readRotors(allRotors);
            return new Machine(_alphabet, numRotors, numPawls, allRotors);
        } catch (NoSuchElementException excp) {
            throw error("configuration file truncated");
        }
    }

    /**
     * Read the name type and notches in config.
     * @param allRotors is passed to read Rotors
     */
    private void readRotors(ArrayList<Rotor> allRotors) {
        temp = _config.next();
        while (_config.hasNext()) {
            String name = temp;
            String next = _config.next();
            char type = next.charAt(0);
            String notches = next.substring(1);
            Rotor newRotor = readRotor(name, type, notches);
            if (newRotor != null) {
                allRotors.add(newRotor);
            }
        }
    }

    /**
     * Return a rotor, reading its description from _config.
     * @param name name is passed to read the rotor.
     * @param type type is passed to read the rotor.
     * @param notches noteches is passed to read the rotor.
     */
    private Rotor readRotor(String name, char type, String notches) {
        try {
            StringBuilder sb = new StringBuilder();
            temp = _config.next();
            while (temp.contains("(")) {
                sb.append(temp);
                sb.append(" ");
                if (!_config.hasNext()) {
                    break;
                }
                temp = _config.next();
            }

            String permutations = sb.toString();
            Permutation p = new Permutation(permutations, _alphabet);
            switch (type) {
            case 'M':
                return new MovingRotor(name, p, notches);
            case 'N':
                return new FixedRotor(name, p);
            default:
                return new Reflector(name, p);
            }
        } catch (NoSuchElementException excp) {
            throw error("bad rotor description");
        }
    }

    /**
     * Set M according to the specification given on SETTINGS,
     * which must have the format specified in the assignment.
     */
    private void setUp(Machine M, String settings) {
        String[] rotors = new String[M.numRotors()];
        String[] setting = settings.split(" ");
        if (M.numRotors() >= 0) {
            for (int i = 1; i <= M.numRotors(); i++) {
                rotors[i - 1] = setting[i];
            }
        }
        StringBuilder sb = new StringBuilder();
        int index = M.numRotors() + 2;
        for (int i = index; i < setting.length; i++) {
            sb.append(setting[i]);
            sb.append(" ");
        }
        M.insertRotors(rotors);
        M.setRotors(setting[M.numRotors() + 1]);
        Permutation perm = new Permutation(sb.toString(), _alphabet);
        M.setPlugboard(perm);
    }

    /**
     * Print MSG in groups of five (except that the last group may
     * have fewer letters).
     */
    private void printMessageLine(String msg) {
        msg = msg.replaceAll(" ", "");
        StringBuilder message = new StringBuilder();

        for (int j = 0; j < msg.length(); j++) {
            message.append(msg.charAt(j));
            if (j % 5 == 4) {
                message.append(" ");
            }
        }
        _output.print(message.toString().trim());
    }

    /**
     * Alphabet used in this machine.
     */
    private Alphabet _alphabet;

    /**
     * Source of input messages.
     */
    private Scanner _input;

    /**
     * Source of machine configuration.
     */
    private Scanner _config;

    /**
     * File for encoded/decoded messages.
     */
    private PrintStream _output;

    /**
     * Temporarily stores next line.
     */
    private String temp;
}
