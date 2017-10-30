package com.enonic.xp.index;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringEscapeUtils;

import com.enonic.xp.data.Value;
import com.enonic.xp.data.ValueFactory;
import com.enonic.xp.data.ValueType;
import com.enonic.xp.data.ValueTypes;

    final class HtmlStripper
    implements IndexValueProcessor
{
    public final static String NAME = "htmlStripper";

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
        final String unescapedHtml = StringEscapeUtils.unescapeHtml( strippedHtml );

        final ValueType valueType = value.getType();
        if ( valueType == ValueTypes.XML )
        {
            return ValueFactory.newXml( unescapedHtml );
        }
        else
        {
            return ValueFactory.newString( unescapedHtml );
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
