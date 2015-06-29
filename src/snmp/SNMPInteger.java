
package snmp;



import java.math.*;
import java.io.*;



/** Defines an arbitrarily-sized integer value; there is no limit on size due to the use
* of Java.lang.BigInteger to store the value internally. For an indicator which "pegs" at its
* maximum value if initialized with a larger value, use SNMPGauge32; for a counter which wraps,
* use SNMPCounter32 or SNMPCounter64.
* @see snmp.SNMPCounter32
* @see snmp.SNMPGauge32
* @see snmp.SNMPCounter64
*/


public class SNMPInteger extends SNMPObject
{
    protected BigInteger value;
    protected byte tag = SNMPBERCodec.SNMPINTEGER;

    /** Initialize value to 0.
    */
    public SNMPInteger()
    {
        this(0);    // initialize value to 0
    }

    public SNMPInteger(long value)
    {
        this.value = new BigInteger(new Long(value).toString());
    }

    public SNMPInteger(String value){

        this.value = new BigInteger(value);
    }

    public SNMPInteger(BigInteger value)
    {
        this.value = value;
    }




    /** Used to initialize from the BER encoding, usually received in a response from
    * an SNMP device responding to an SNMPGetRequest.
    * @throws SNMPBadValueException Indicates an invalid BER encoding supplied. Shouldn't
    * occur in normal operation, i.e., when valid responses are received from devices.
    */

    protected SNMPInteger(byte[] enc)
        throws SNMPBadValueException
    {
        extractValueFromBEREncoding(enc);
    }




    /** Returns a java.lang.BigInteger object with the current value.
    */

    public Object getValue()
    {
        return value;
    }





    /** Used to set the value with an instance of java.lang.Integer or
    * java.lang.BigInteger.
    * @throws SNMPBadValueException Indicates an incorrect object type supplied.
    */

    public void setValue(Object newValue)
        throws SNMPBadValueException
    {
        if (newValue instanceof BigInteger)
            value = (BigInteger)newValue;
        else if (newValue instanceof Integer)
            value = new BigInteger(((Integer)newValue).toString());
        else if (newValue instanceof String)
            value = new BigInteger((String)newValue);
        else

            throw new SNMPBadValueException(" Integer: bad object supplied to set value ");
    }




    /** Returns the full BER encoding (type, length, value) of the SNMPInteger subclass.
    */

    public byte[] getBEREncoding()
    {
        ByteArrayOutputStream outBytes = new ByteArrayOutputStream();

        // write contents
        // boy, was THIS easy! Love that Java!
        byte[] data = value.toByteArray();


        for(int i = 0; i < data.length; i++)
        {
             System.out.println("data~~: " + Integer.toHexString(data[i] & 0xFF));
        }
        // calculate encoding for length of data
        byte[] len = SNMPBERCodec.encodeLength(data.length);
        System.out.println("length~~: " + Integer.toHexString(len[0]));
        System.out.println("tag ~~" + Integer.toHexString(tag));
        // encode T,L,V info
        outBytes.write(tag);
        
        outBytes.write(len, 0, len.length);
        outBytes.write(data, 0, data.length);

        return outBytes.toByteArray();
    }




    /** Used to extract a value from the BER encoding of the value. Called in constructors for
    * SNMPInteger subclasses.
    * @throws SNMPBadValueException Indicates an invalid BER encoding supplied. Shouldn't
    * occur in normal operation, i.e., when valid responses are received from devices.
    */

    public void extractValueFromBEREncoding(byte[] enc)
        throws SNMPBadValueException
    {
        try
        {
            value = new BigInteger(enc);
        }
        catch (NumberFormatException e)
        {
            throw new SNMPBadValueException(" Integer: bad BER encoding supplied to set value ");
        }
    }



    public String toString()
    {
        return value.toString();
        // return new String(value.toString());
    }



    public String toString(int radix)
    {
        return value.toString(radix);
        // return new String(value.toString());
    }



}