package com.enonic.xp.index;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.enonic.xp.data.Value;
import com.enonic.xp.data.ValueType;
import com.enonic.xp.data.ValueTypes;

final class HtmlStripper
    implements IndexValueProcessor
{
    private final static String NAME = "htmlStripper";

    private final static Pattern XML_TAG_PATTERN = Pattern.compile( "(?:<[^>]*>)+", Pattern.MULTILINE );

    @Override
    public Value process( final Value value )
    {
        if ( value == null || !value.isText() )
        {
            return value;
        }

        final Matcher matcher = XML_TAG_PATTERN.matcher( value.toString() );
        final String strippedHtml = matcher.replaceAll( " " );

        final ValueType valueType = value.getType();
        if ( valueType == ValueTypes.XML )
        {
            return Value.newXml( strippedHtml );
        }
        else
        {
            return Value.newString( strippedHtml );
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
