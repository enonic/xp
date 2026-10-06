package com.enonic.xp.core.internal.parser;

public interface HtmlElementInternal
{
    String getTagName();

    boolean hasAttribute( String attributeName );

    String getAttribute( String attributeName );

    void remove();

    HtmlElementInternal setAttribute( String attributeName, String value );

    HtmlElementInternal setAttribute( String attributeName, boolean value );

    HtmlElementInternal removeAttribute( String attributeName );

    /**
     * Sets an attribute right after another one, or last when the element does not have the other one.
     */
    HtmlElementInternal setAttributeAfter( String previousAttributeName, String attributeName, String value );
}
