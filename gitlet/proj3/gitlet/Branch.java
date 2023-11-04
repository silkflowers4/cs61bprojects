package gitlet;

import java.io.Serializable;

/**
 * Branch object.
 * @author Joshua park
 */
class Branch implements Serializable {

    /**
     * Name of the branch.
     */
    private String realName;
    /**
     * Which commit in the branch we are at.
     */
    private String _sha1;


    /**
     * Branch constructor.
     * @param name name of the branch
     * @param sha1 which commit in the branch we are at
     */
    Branch(String name, String sha1) {
        realName = name;
        _sha1 = sha1;
        this.serialize();
    }

    /**
     * Serialize the branch.
     */
    private void serialize() {
        Utils.writeObject(Utils.join(Paths.BRANCHES, this.realName), this);
    }

    /**
     * name getter.
     * @return real Name
     */
    public String getName() {
        return realName;
    }

    /**
     * sha 1 getter.
     * @return sha1
     */
    public String getSha1() {
        return _sha1;
    }

    /**
     * sha1 setter.
     * @param sha1 return
     */
    public void setSha1(String sha1) {
        this._sha1 = sha1;
        this.serialize();
    }
}
