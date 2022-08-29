package com.enonic.xp.portal.impl.html;

import java.util.List;
import java.util.stream.Collectors;

import com.enonic.xp.core.internal.parser.HtmlDocumentInternal;
import com.enonic.xp.core.internal.parser.HtmlElementInternal;
import com.enonic.xp.core.internal.parser.HtmlParserInternal;
import com.enonic.xp.portal.html.HtmlDocument;
import com.enonic.xp.portal.html.HtmlElement;

public class HtmlParser
{
    private HtmlParser()
    {

    }

    public static HtmlDocument parse( final String html )
    {
        final HtmlDocumentInternal document = HtmlParserInternal.parse( html );

        return new HtmlDocumentImpl( document );
    }

    private static class HtmlDocumentImpl
        implements HtmlDocument
    {
        private final HtmlDocumentInternal document;

        public HtmlDocumentImpl( final HtmlDocumentInternal document )
        {
            this.document = document;
        }

        @Override
        public List<HtmlElement> select( final String cssSelector )
        {
            return document.select( cssSelector ).stream().map( this::map ).collect( Collectors.toList() );
        }

        @Override
        public String getInnerHtml()
        {
            return document.getInnerHTML();
        }

        @Override
        public String getInnerHtmlByTagName( final String tagName )
        {
            return document.getInnerHtmlByTagName( tagName );
        }

        private HtmlElement map( HtmlElementInternal element )
        {
            return new HtmlElement()
            {

                @Override
                public String getTagName()
                {
                    return element.getTagName();
                }

                @Override
                public boolean hasAttribute( final String attributeName )
                {
                    return element.hasAttribute( attributeName );
                }

                @Override
                public String getAttribute( final String attributeName )
                {
                    return element.getAttribute( attributeName );
                }

                @Override
                public void remove()
                {
                    element.remove();
                }

                @Override
                public HtmlElement setAttribute( final String attributeName, final String value )
                {
                    return map( element.setAttribute( attributeName, value ) );
                }

                @Override
                public HtmlElement setAttribute( final String attributeName, final boolean value )
                {
                    return map( element.setAttribute( attributeName, value ) );
                }

                @Override
                public HtmlElement removeAttribute( final String attributeName )
                {
                    return map( element.removeAttribute( attributeName ) );
                }
            };
        }
    }
}
