package com.enonic.wem.xslt.internal.function;

import java.util.List;

import com.google.common.collect.Lists;

import net.sf.saxon.lib.ExtensionFunctionCall;
import net.sf.saxon.om.Item;
import net.sf.saxon.om.Sequence;
import net.sf.saxon.om.SequenceIterator;
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

    protected final String[] toStringArray( final Sequence seq )
        throws XPathException
    {
        final SequenceIterator it = seq.iterate();
        final List<String> list = Lists.newArrayList();

        while ( true )
        {
            final Item current = it.next();
            if ( current == null )
            {
                break;
            }

            list.add( current.getStringValue() );
        }

        return list.toArray( new String[list.size()] );
    }

    protected final Item createValue( final String value )
    {
        return new StringValue( value );
    }
}
