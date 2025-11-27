package com.enonic.xp.schema;

public record LocalizedText(String text, String i18n)
{
    public LocalizedText( String text )
    {
        this( text, null );
    }
}
