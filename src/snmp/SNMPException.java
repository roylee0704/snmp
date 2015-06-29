

package snmp;


/**
*    Exception thrown when attempt to set or get value of SNMP OID fails. Reason could be that
*    specified variable not supported by device, or that supplied community name has insufficient
*    privileges.
*/

public class SNMPException extends Exception
{

    public SNMPException()
    {
        super();
    }


    /**
    *    Create exception with message string.
    */

    public SNMPException(String s)
    {
        super(s);
    }


}