package com.enonic.xp.schema;

import com.enonic.xp.util.GenericValue;

public record LocalizedText(String text, String i18n)
{
    public LocalizedText( String text )
    {
        this( text, null );
    }

    public static LocalizedText from( GenericValue value )
    {
        return switch ( value.getType() )
        {
            case STRING -> new LocalizedText( value.asString() );
            case OBJECT -> new LocalizedText( value.property( "text" ).asString(),
                                              value.optional( "i18n" ).map( GenericValue::asString ).orElse( null ) );
            default -> throw new IllegalArgumentException( "Unsupported value type: " + value.getType() );
        };
    }
}
