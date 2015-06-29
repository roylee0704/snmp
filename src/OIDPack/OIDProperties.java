package OIDPack;

/**
 *
 * @author roylee
 */
public class OIDProperties {
    public static final int NODEINDEX = 0;
    public static final int ACCESS = 1;
    public static final int TYPE = 2;
    public static final int DESCRIPTION = 3;
    public static final int NAME = 4;
    public static final int PNODEINDEX = 5;

    private String[] oidAttr = new String[6];

    public OIDProperties(){
    }


    public OIDProperties(String newNODEINDEX, String newACCESS, String newTYPE, String newDESCRIPTION, String newNAME, String newPNodeIndex){
        oidAttr[NODEINDEX] = newNODEINDEX;
        oidAttr[ACCESS] = newACCESS;
        oidAttr[TYPE] = newTYPE;
        oidAttr[DESCRIPTION] = newDESCRIPTION;
        oidAttr[NAME] = newNAME;
        oidAttr[PNODEINDEX] = newPNodeIndex;
    }

    public String getOIDProperty(int oidPropertyIndex){
        return oidAttr[oidPropertyIndex];
    }
}
