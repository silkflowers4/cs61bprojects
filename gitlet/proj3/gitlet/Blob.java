package gitlet;

import java.io.Serializable;

/**
 * The blob class, implements serializable.
 * @author Joshua Park
 */
public class Blob implements Serializable {

    /**
     * SHA1 code for the commit.
     */
    private String _sha1;

    /**
     * Filename associated with this blob.
     */
    private String _filename;

    /**
     * Actual string contents of the file.
     */
    private String _contents;

    /**
     * the blob constructor.
     * @param content input string
     * @param filename input filename
     */
    Blob(String content, String filename) {
        _sha1 = Utils.sha1(content, filename);
        _filename = filename;
        _contents = content;
    }

    /**
     * sha getter.
     * @return _sha1
     */
    public String getSha1() {
        return _sha1;
    }

    /**
     * get File name.
     * @return file name
     */
    public String getFilename() {
        return _filename;
    }

    /**
     * get Contents.
     * @return contents
     */
    public String getContents() {
        return _contents;
    }

    /**
     * Created blob may not be used immediately.
     * Caller will serialize if it decides the blob should stay.
     */
    public void callerSerialize() {
        Utils.writeObject(Utils.join(Paths.BLOBS, _sha1), this);
    }
}
