package com.enonic.xp.portal.impl.xslt.function;

import net.sf.saxon.lib.ExtensionFunctionCall;
import net.sf.saxon.om.Item;
import net.sf.saxon.om.Sequence;
import net.sf.saxon.trans.XPathException;
import net.sf.saxon.value.StringValue;

abstract class AbstractFunctionCall
    extends ExtensionFunctionCall
{
    protected final String toSingleString( final Sequence arg )
        throws XPathException
    {
        final Item item = arg.head();
        if ( item == null )
        {
            return null;
        }
        else
        {
            return item.getStringValue();
        }
    }

    protected final Item createValue( final String value )
    {
        return new StringValue( value );
    }
}
