package com.enonic.xp.portal.html;

import java.util.List;


public interface HtmlDocument
{
    List<HtmlElement> select( String cssSelector );

    String getInnerHtml();
}
