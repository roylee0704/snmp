

package snmp;


/**
*    Exception thrown when attempt to set the value of an SNMP OID on a device fails. Reason could be
*    that specified variable not supported by device, or that supplied community name has insufficient
*    privileges.
*/

public class SNMPSetException extends SNMPRequestException
{

    /*
    public SNMPSetException()
    {
        super();
    }
    */

    /**
    *    Create exception with message string.
    */
    /*
    public SNMPSetException(String s)
    {
        super(s);
    }
    */

    /**
    *    Create exception with errorIndex and errorStatus
    */

    public SNMPSetException(int errorIndex, int errorStatus)
    {
        super(errorIndex, errorStatus);
    }


    /**
    *    Create exception with errorIndex, errorStatus and message string
    */

    public SNMPSetException(String message, int errorIndex, int errorStatus)
    {
        super(message, errorIndex, errorStatus);
    }

}