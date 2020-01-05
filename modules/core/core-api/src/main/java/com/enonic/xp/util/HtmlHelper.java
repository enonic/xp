package com.enonic.xp.util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.parser.Parser;
import org.jsoup.parser.Tag;
import org.jsoup.select.NodeTraversor;
import org.jsoup.select.NodeVisitor;

public final class HtmlHelper
{
    private HtmlHelper()
    {
    }

    public static String unescape( String html )
    {
        if ( html.isBlank() )
        {
            return html;
        }
        return Parser.unescapeEntities( html, false );
    }

    /**
     * Extracts text from HTML. Whitespace is normalized and trimmed.
     *
     * @param html string with HTML, can't be null
     * @return extracted text
     */
    public static String htmlToText( String html )
    {
        if ( html.isBlank() )
        {
            return "";
        }
        return Jsoup.parse( html ).text();
    }

    /**
     * Simulates regex like (?:<[^>]*>)+ HTML tag stripper where tags replaced with space.
     * But does not fail on technically valid cases like &lt;a about="&gt;"text&lt;/a&gt;
     *
     * @param html string with HTML, can't be null
     * @return extracted text
     */
    public static String htmlToTextLegacy( String html )
    {
        if ( html.isBlank() )
        {
            return html;
        }

        final StringBuilder stringBuilder = new StringBuilder();

        NodeTraversor.traverse( new HtmlStripNodeVisitor( stringBuilder ), Jsoup.parse( html ) );

        return stringBuilder.toString();
    }

    private static class HtmlStripNodeVisitor
        implements NodeVisitor
    {
        private static final Tag BODY_TAG = Tag.valueOf( "body" );

        private static final Tag HEAD_TAG = Tag.valueOf( "head" );

        private static final String SPACE = " ";

        private final StringBuilder builder;

        public HtmlStripNodeVisitor( final StringBuilder builder )
        {
            this.builder = builder;
        }

        private boolean delimited = false;

        public void head( Node node, int depth )
        {
            if ( depth == 0 )
            {
                return; // skip root tag
            }
            if ( node instanceof TextNode )
            {
                final TextNode textNode = (TextNode) node;
                final String wholeText = textNode.getWholeText();
                final Node parent = textNode.parent();
                final boolean inBody = parent instanceof Element && ( (Element) parent ).tag().equals( BODY_TAG );

                // All text surrounded by tag (except body tag) wrapped with space.
                final boolean surround = !wholeText.isBlank() && !inBody;

                if ( surround )
                {
                    builder.append( SPACE );
                }
                builder.append( wholeText );
                if ( surround )
                {
                    builder.append( SPACE );
                    delimited = true;
                }

                delimited = surround;
            }
            else if ( node instanceof Element )
            {
                final Element element = (Element) node;
                // empty tags (except head tag) replaced with space.
                if ( !delimited && element.childNodeSize() == 0 && !element.tag().equals( HEAD_TAG ) )
                {
                    builder.append( SPACE );
                    delimited = true;
                }
            }
            else
            {
                // comments replaced with space.
                builder.append( SPACE );
                delimited = true;
            }
        }

        public void tail( Node node, int depth )
        {
        }
    }
}
