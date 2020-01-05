package com.enonic.xp.index;

import com.enonic.xp.data.Value;
import com.enonic.xp.data.ValueFactory;
import com.enonic.xp.data.ValueTypes;
import com.enonic.xp.util.HtmlHelper;

final class HtmlStripper
    implements IndexValueProcessor
{
    public final static String NAME = "htmlStripper";

    @Override
    public Value process( final Value value )
    {
        if ( value == null || !value.isText() )
        {
            return value;
        }

        final String htmlStripped = HtmlHelper.htmlToTextLegacy( value.asString() );

        if ( ValueTypes.XML.equals( value.getType() ) )
        {
            return ValueFactory.newXml( htmlStripped );
        }
        else
        {
            return ValueFactory.newString( htmlStripped );
        }
    }

    @Override
    public String getName()
    {
        return NAME;
    }

    @Override
    public String toString()
    {
        return NAME;
    }

    @Override
    public boolean equals( final Object obj )
    {
        if ( this == obj )
        {
            return true;
        }
        if ( obj instanceof IndexValueProcessor )
        {
            IndexValueProcessor indexValueProcessor = (IndexValueProcessor) obj;
            return NAME.equals( indexValueProcessor.getName() );
        }
        return false;
    }

    @Override
    public int hashCode()
    {
        return NAME.hashCode();
    }
}
