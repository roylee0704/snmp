package DriverPack;

/**
 *
 * @author roylee
 */
public class DriverValue {
    private static final int NODEINDEX = 0;
    private static final int VALUE = 1;
    private String[] drvValue = new String[2];

    public DriverValue(String newNodeIndex, String newValue){
        drvValue[NODEINDEX] = newNodeIndex;
        drvValue[VALUE] = newValue;
    }

    public void setValue(String newValue){
        drvValue[VALUE] = newValue;
    }

    public String getValue(){
        return drvValue[VALUE];
    }

    public String[] getDriverValue(){
        return drvValue;
    }

    public String getValueNodeIndex(){
        return drvValue[NODEINDEX];
    }

}
