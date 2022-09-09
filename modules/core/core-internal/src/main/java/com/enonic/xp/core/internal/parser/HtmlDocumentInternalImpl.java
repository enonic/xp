package com.enonic.xp.core.internal.parser;

import java.util.List;
import java.util.stream.Collectors;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class HtmlDocumentInternalImpl
    implements HtmlDocumentInternal
{
    private final Document document;

    public HtmlDocumentInternalImpl( final Document document )
    {
        this.document = document;
        this.document.outputSettings().prettyPrint( false ).outline( false );
    }

    @Override
    public List<HtmlElementInternal> select( final String cssSelector )
    {
        return document.select( cssSelector ).stream().map( HtmlDocumentInternalImpl::map ).collect( Collectors.toList() );
    }

    @Override
    public String getInnerHTML()
    {
        return getInnerHtmlByTagName( "body" );
    }

    @Override
    public String getInnerHtmlByTagName( final String tagName )
    {
        return document.getElementsByTag( tagName ).html();
    }

    private static HtmlElementInternal map( final Element element )
    {
        return new HtmlElementInternalImpl( element );
    }

    private static class HtmlElementInternalImpl
        implements HtmlElementInternal
    {
        final Element element;

        HtmlElementInternalImpl( final Element element )
        {
            this.element = element;
        }

        @Override
        public String getTagName()
        {
            return element.tagName();
        }

        @Override
        public boolean hasAttribute( final String attributeName )
        {
            return element.hasAttr( attributeName );
        }

        @Override
        public String getAttribute( final String attributeName )
        {
            return element.attr( attributeName );
        }

        @Override
        public void remove()
        {
            element.remove();
        }

        @Override
        public HtmlElementInternal setAttribute( final String attributeName, final String value )
        {
            return map( element.attr( attributeName, value ) );
        }

        @Override
        public HtmlElementInternal setAttribute( final String attributeName, final boolean value )
        {
            return map( element.attr( attributeName, value ) );
        }

        @Override
        public HtmlElementInternal removeAttribute( final String attributeName )
        {
            return map( element.removeAttr( attributeName ) );
        }
    }
}
