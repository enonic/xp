package com.enonic.xp.portal.html;

import java.util.List;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public interface HtmlDocument
{
    List<HtmlElement> select( String cssSelector );

    String getInnerHtml();
}
