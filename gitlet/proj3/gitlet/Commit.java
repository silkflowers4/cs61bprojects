package gitlet;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

/**
 * The commit class, works on all the commit.
 * @author joshpark
 */
public class Commit implements Serializable {
    /**
     * Parent node's sha1.
     */
    private String _parent1;
    /**
     * Optional second parent node's sha1.
     */
    private String _parent2;
    /**
     * Commit message.
     */
    private String _commitMessage;
    /**
     * Time stamp.
     */
    private String _timeStamp;
    /**
     * Mappings of filename to blobIds.
     */
    private HashMap<String, String> _state;
    /**
     * SHA1 code for the commit.
     */
    private String _sha1;
    /**
     * The format for the string form of the date.
     */
    private SimpleDateFormat _format =
            new SimpleDateFormat("E MMM dd HH:mm:ss yyyy Z");


    /**
     * Initial commit constructor.
     **/
    Commit() {
        _parent1 = null;
        _parent2 = null;
        _commitMessage = "initial commit";
        _timeStamp = _format.format(new Date(0));
        _state = new HashMap<>();
        _sha1 = Utils.sha1(_state.toString(), _commitMessage, _timeStamp);
        serialize();
    }

    /**
     * Initial commit constructor.
     * @param state state
     * @param parent2 parent2
     * @param commitMessage commitMessage
     * @param parent1 parent1
     **/
    Commit(String parent1, String parent2, String commitMessage,
           HashMap<String, String> state) {
        _parent1 = parent1;
        _parent2 = parent2;
        _commitMessage = commitMessage;
        _timeStamp = _format.format(new Date());
        _state = state;
        _sha1 = Utils.sha1(_state.toString(), _commitMessage, _timeStamp);
        serialize();
    }

    /**
     * Serialize the commit.
     */
    private void serialize() {
        Utils.writeObject(Utils.join(Paths.COMMITS, this._sha1), this);
    }

    /**
     * get Sha1.
     * @return sha1
     */
    public String getSha1() {
        return this._sha1;
    }

    /**
     * get Parent.
     * @return parent
     */
    public String getParent1() {
        return _parent1;
    }

    /**
     * get parent 2.
     * @return parent
     */
    public String getParent2() {
        return _parent2;
    }

    /**
     * get State.
     * @return get State
     */
    public HashMap<String, String> getState() {
        return _state;
    }

    /**
     * get commit message.
     * @return commit message
     */
    public String getCommitMessage() {
        return _commitMessage;
    }

    /**
     * get Time stamp.
     * @return time stamp
     */
    public String getTimeStamp() {
        return _timeStamp;
    }

    /**
     * get Blob.
     * @param filename input file
     * @return read object of blob
     */
    public Blob getBlob(String filename) {
        String sha1 = _state.get(filename);
        return Utils.readObject(Utils.join(Paths.BLOBS, sha1), Blob.class);
    }
}
