

package snmp;

import java.util.*;




/**
*    The SNMPVariablePair class implements the VarBind specification detailed below from RFC 1157.
*    It is a specialization of SNMPSequence, defining a 2-element sequence containing a single
*    (object identifier, value) pair. Note that the values are themselves SNMPObjects.


          -- variable bindings

          VarBind ::=
                  SEQUENCE {
                      name
                          ObjectName,

                      value
                          ObjectSyntax
                  }



*/


public class SNMPVariablePair extends SNMPSequence
{

    /**
    *    Create a new variable pair having the supplied object identifier and vale.
    */

    public SNMPVariablePair(SNMPObjectIdentifier objectID, SNMPObject value)
        throws SNMPBadValueException
    {
        super();
        Vector contents = new Vector();
        contents.insertElementAt(objectID, 0);
        contents.insertElementAt(value, 1);
        this.setValue(contents);// this. = parent's method, insert into vector.!!!
    }



}