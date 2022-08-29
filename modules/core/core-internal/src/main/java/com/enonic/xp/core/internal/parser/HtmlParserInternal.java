package com.enonic.xp.core.internal.parser;

import org.jsoup.Jsoup;

public class HtmlParserInternal
{
    private HtmlParserInternal()
    {
    }

    public static HtmlDocumentInternal parse( final String html )
    {
        return new HtmlDocumentInternalImpl( Jsoup.parse( html ) );
    }
}
