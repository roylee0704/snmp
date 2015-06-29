

package snmp;


/**
*    Utility class holding components of an ASN.1 (type, length, value) triple.
*/

public class SNMPTLV
{
    public byte tag;
    public int totalLength;
    public byte[] value;
}