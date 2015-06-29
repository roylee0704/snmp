


package snmp;


/**
*    Exception thrown when request to get or set the value of an SNMP OID on a device fails. Reason could be
*    that specified variable not supported by device, or that supplied community name has insufficient
*    privileges. errorStatus parameter allows the reason for the failure to be specified, and errorIndex
*   allows the index of the failed OID to be specified.
*/

public class SNMPRequestException extends SNMPException
{

    public static final int NO_ERROR = 0;
    public static final int VALUE_TOO_BIG = 1;
    public static final int VALUE_NOT_AVAILABLE = 2;
    public static final int BAD_VALUE = 3;
    public static final int VALUE_READ_ONLY = 4;
    public static final int FAILED = 5;

    public int errorIndex = 0;
    public int errorStatus = 0;



    /**
    *    Create exception with errorIndex, errorStatus
    */

    public SNMPRequestException(int errorIndex, int errorStatus)
    {
        super();

        this.errorIndex = errorIndex;
        this.errorStatus = errorStatus;
    }



    /**
    *    Create exception with errorIndex, errorStatus, and message string
    */

    public SNMPRequestException(String message, int errorIndex, int errorStatus)
    {
        super(message);

        this.errorIndex = errorIndex;
        this.errorStatus = errorStatus;
    }

}