
package snmp;

import java.io.*;



/**
*    Used when an unknown SNMP object type is encountered. Just takes a byte array
*    for its constructor, and uses this as raw bytes.
*/

public class SNMPUnknownObject extends SNMPObject
{
    private byte[] data;

    protected byte tag = SNMPBERCodec.SNMPUNKNOWNOBJECT;

    /**
    *    Just takes a byte array, and uses this as raw bytes.
    */
    public SNMPUnknownObject(byte[] enc)
    {
        data = enc;
    }




    /**
    *    Return a byte array containing the raw bytes supplied.
    */
    public Object getValue()
    {
        return data;
    }




    /**
    *    Takes a byte array containing the raw bytes stored as the value.
    */

    public void setValue(Object data)
        throws SNMPBadValueException
    {
        if (data instanceof byte[])
            this.data = (byte[])data;
        else
            throw new SNMPBadValueException(" Unknown Object: bad object supplied to set value ");
    }





    /**
    *    Return the BER encoding of this object.
    */

    protected byte[] getBEREncoding()
    {

        ByteArrayOutputStream outBytes = new ByteArrayOutputStream();

        byte type = SNMPBERCodec.SNMPUNKNOWNOBJECT;

        // calculate encoding for length of data
        byte[] len = SNMPBERCodec.encodeLength(data.length);

        // encode T,L,V info
        outBytes.write(type);
        outBytes.write(len, 0, len.length);
        outBytes.write(data, 0, data.length);

        return outBytes.toByteArray();
    }




    /**
    *    Return String created from raw bytes of this object.
    */

    public String toString()
    {
        return new String(data);
    }



}