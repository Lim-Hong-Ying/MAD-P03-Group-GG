package sg.edu.np.mad_p03_group_gg;

public class listingObject {
    private String title; //listing title
    private String tURL; //thumbnail URL
    private String sID; //seller ID
    private String sPPU; //seller profile picture

    public listingObject() {

    }

    public listingObject(String t, String turl, String sid, String sppu) {
        setTitle(t);
        settURL(turl);
        setSID(sid);
        setSPPU(sppu);
    }

    public void setTitle(String s) {
        title = s;
    }

    public String getTitle() {
        return title;
    }

    public void settURL(String s) {
        tURL = s;
    }

    public String getttURL() {
        return tURL;
    }

    public void setSID(String s) {
        sID = s;
    }

    public String getSID() {
        return sID;
    }

    public void setSPPU(String s) {
        sPPU = s;
    }

    public String getSPPU() {
        return sPPU;
    }
}
