package com.enonic.xp.core.internal.parser;

import java.util.List;

public interface HtmlDocumentInternal
{
    List<HtmlElementInternal> select( String cssSelector );

    String getInnerHTML();

    String getInnerHtmlByTagName( String tagName );
}
