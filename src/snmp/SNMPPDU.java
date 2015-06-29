


package snmp;

import java.util.*;
import java.math.*;




/**
*    The SNMPPDU class represents an SNMP PDU from RFC 1157, as indicated below. This
*    forms the payload of an SNMP message.

-- protocol data units

          PDUs ::=
                  CHOICE {
                              get-request
                                  GetRequest-PDU,

                              get-next-request
                                  GetNextRequest-PDU,

                              get-response
                                  GetResponse-PDU,

                              set-request
                                  SetRequest-PDU,

                              trap
                                  Trap-PDU
                          }

          -- PDUs

          GetRequest-PDU ::=
              [0]
                  IMPLICIT PDU

          GetNextRequest-PDU ::=
              [1]
                  IMPLICIT PDU

          GetResponse-PDU ::=
              [2]
                  IMPLICIT PDU

          SetRequest-PDU ::=
              [3]
                  IMPLICIT PDU

          PDU ::=
                  SEQUENCE {
                     request-id
                          INTEGER,

                      error-status      -- sometimes ignored
                          INTEGER {
                              noError(0),
                              tooBig(1),
                              noSuchName(2),
                              badValue(3),
                              readOnly(4),
                              genErr(5)
                          },

                      error-index       -- sometimes ignored
                         INTEGER,

                      variable-bindings -- values are sometimes ignored
                          VarBindList
                  }



          -- variable bindings

          VarBind ::=
                  SEQUENCE {
                      name
                          ObjectName,

                      value
                          ObjectSyntax
                  }

         VarBindList ::=
                  SEQUENCE OF
                     VarBind

         END

*/


public class SNMPPDU extends SNMPSequence
{


    /**
    *    Create a new PDU of the specified type, with given request ID, error status, and error index,
    *    and containing the supplied SNMP sequence as data.
    */


    public SNMPPDU(byte pduType, int requestID, int errorStatus, int errorIndex, SNMPSequence varList)
        throws SNMPBadValueException
    {
        super();
        Vector contents = new Vector();
        tag = pduType;
        contents.insertElementAt(new SNMPInteger(requestID), 0);
        contents.insertElementAt(new SNMPInteger(errorStatus), 1);
        contents.insertElementAt(new SNMPInteger(errorIndex), 2);
        contents.insertElementAt(varList, 3);
        this.setValue(contents);
    }




    /**
    *    Create a new PDU of the specified type from the supplied BER encoding.
    *    @throws SNMPBadValueException Indicates invalid SNMP PDU encoding supplied in enc.
    */

    protected SNMPPDU(byte[] enc, byte pduType)
        throws SNMPBadValueException
    {
        tag = pduType;
        extractFromBEREncoding(enc);

        // validate the message: make sure we have the appropriate pieces
        Vector contents = (Vector)(this.getValue());

        if (contents.size() != 4)
        {
            throw new SNMPBadValueException("Bad PDU");
        }

        if (!(contents.elementAt(0) instanceof SNMPInteger))
        {
            throw new SNMPBadValueException("Bad PDU: bad request ID");
        }

        if (!(contents.elementAt(1) instanceof SNMPInteger))
        {
            throw new SNMPBadValueException("Bad PDU: bad error status");
        }

        if (!(contents.elementAt(2) instanceof SNMPInteger))
        {
            throw new SNMPBadValueException("Bad PDU: bad error index");
        }

        if (!(contents.elementAt(3) instanceof SNMPSequence))
        {
            throw new SNMPBadValueException("Bad PDU: bad variable binding list");
        }

        // now validate the variable binding list: should be list of sequences which
        // are (OID, value) pairs
        SNMPSequence varBindList = this.getVarBindList();
        for (int i = 0; i < varBindList.size(); i++)
        {
            SNMPObject element = varBindList.getSNMPObjectAt(i);

            // must be a two-element sequence
            if (!(element instanceof SNMPSequence))
            {
                throw new SNMPBadValueException("Bad PDU: bad variable binding at index" + i);
            }

            // variable binding sequence must have 2 elements, first of which must be an object identifier
            SNMPSequence varBind = (SNMPSequence)element;
            if ((varBind.size() != 2) || !(varBind.getSNMPObjectAt(0) instanceof SNMPObjectIdentifier))
            {
                throw new SNMPBadValueException("Bad PDU: bad variable binding at index" + i);
            }
        }


    }




    /**
    *    A utility method that extracts the variable binding list from the pdu. Useful for retrieving
    *    the set of (object identifier, value) pairs returned in response to a request to an SNMP
    *    device. The variable binding list is just an SNMP sequence containing the identifier, value pairs.
    *    @see snmp.SNMPVarBindList
    */

    public SNMPSequence getVarBindList()
    {
        Vector contents = (Vector)(this.getValue());
        return (SNMPSequence)(contents.elementAt(3));
    }



    /**
    *    A utility method that extracts the request ID number from this PDU.
    */

    public int getRequestID()
    {
        Vector contents = (Vector)(this.getValue());
        return ((BigInteger)((SNMPInteger)(contents.elementAt(0))).getValue()).intValue();
    }



    /**
    *    A utility method that extracts the error status for this PDU; if nonzero, can get index of
    *    problematic variable using getErrorIndex().
    */

    public int getErrorStatus()
    {
        Vector contents = (Vector)(this.getValue());
        return ((BigInteger)((SNMPInteger)(contents.elementAt(1))).getValue()).intValue();
    }



    /**
    *    A utility method that returns the error index for this PDU, identifying the problematic variable.
    */

    public int getErrorIndex()
    {
        Vector contents = (Vector)(this.getValue());
        return ((BigInteger)((SNMPInteger)(contents.elementAt(2))).getValue()).intValue();
    }



    /**
    *    A utility method that returns the PDU type of this PDU.
    */

    public byte getPDUType()
    {
        return tag;
    }


}