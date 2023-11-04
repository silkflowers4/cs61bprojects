package gitlet;
import java.io.Serializable;

/**
 * Pointer to the current commit.
 * @author joshua park
 */
class Head implements Serializable {

    /** Branch name. */
    private String _name1;

    /** The commit object. */
    private String commitSha1;

    /**
     * Construct a head pointer.
     * @param name string name of branch
     * @param commitSha1input string commit sha1
     */
    Head(String name, String commitSha1input) {
        _name1 = name;
        commitSha1 = commitSha1input;
        this.serialize();
    }

    /**
     * Serialize the head pointer.
     */
    private void serialize() {
        Utils.writeObject(Paths.HEAD, this);
    }

    /**
     * name getter.
     * @return name
     */
    public String getName() {
        return _name1;
    }

    /**
     * commit setter.
     * @return commit
     */
    public String setCommitSha1() {
        return commitSha1;
    }

    /**
     * set commit.
     * @param commitSha2 String
     */
    public void setCommitSha1(String commitSha2) {
        this.commitSha1 = commitSha2;
        this.serialize();
    }

    /**
     * name setter.
     * @param realName sets name
     */
    public void setName(String realName) {
        this._name1 = realName;
        this.serialize();
    }
}
