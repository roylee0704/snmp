package DriverPack;

import java.util.*;

/**
 *
 * @author roylee
 */

//load from db...always load from db?nope.so put it on agent outest level to keep track, better create a bag pack for it.
public class GenericDriver { //every driver has diff num of attriubte in attribute map.
    private Map AttributeHashMap;

    public GenericDriver(){

    }

    public GenericDriver(int attrID, String nodeIndex, String value){
        AttributeHashMap = new HashMap<Integer, DriverValue>();//attrID and "value", no description, access type ,etc.
        addAttributes(attrID, nodeIndex, value);
       
    }

    public void addAttributes(int attrID, String nodeIndex, String value){
        AttributeHashMap.put(attrID, new DriverValue(nodeIndex, value));
        
    }

    public void delAttributes(int attrID, String value){
       // AttributeHashMap.put(name, value);
    }
    
    public HashMap<Integer, DriverValue> getAttributeHashMap(){
        return (HashMap<Integer, DriverValue>)AttributeHashMap;
    }


    public void setValue(int attrID, String value){
        DriverValue drValue = (DriverValue)AttributeHashMap.get(attrID);
        drValue.setValue(value);
    }

    public String getValue(int attrID){
        DriverValue drValue = (DriverValue)AttributeHashMap.get(attrID);
        return drValue.getValue();
    }

    public String getValueNodeIndex(int attrID){
        DriverValue drValue = (DriverValue)AttributeHashMap.get(attrID);
        return drValue.getValueNodeIndex();
    }

    public int getNumOfAttributePerDriver(){
        return AttributeHashMap.size();
    }
}
