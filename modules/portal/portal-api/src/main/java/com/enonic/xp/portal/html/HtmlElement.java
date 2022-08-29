package com.enonic.xp.portal.html;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public interface HtmlElement
{
    String getTagName();

    boolean hasAttribute( String attributeName );

    String getAttribute( String attributeName );

    void remove();

    HtmlElement setAttribute( String attributeName, String value );

    HtmlElement setAttribute( String attributeName, boolean value );

    HtmlElement removeAttribute( String attributeName );
}
