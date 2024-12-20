package com.enonic.xp.portal.impl.html;

import java.util.ArrayDeque;
import java.util.Deque;

import com.google.common.escape.Escaper;
import com.google.common.html.HtmlEscapers;

public final class HtmlBuilder
{
    private final Escaper escaper;

    private final StringBuilder str;

    private final Deque<String> openTags;

    private boolean addedInner;

    private boolean closedEmptyTag;

    public HtmlBuilder()
    {
        this.escaper = HtmlEscapers.htmlEscaper();
        this.str = new StringBuilder();
        this.openTags = new ArrayDeque<>();
        this.addedInner = false;
        this.closedEmptyTag = false;
    }

    private void closeIfNeeded()
    {
        if ( !this.closedEmptyTag && !this.addedInner && !this.openTags.isEmpty() )
        {
            this.str.append( '>' );
        }
    }

    public HtmlBuilder open( final String name )
    {
        closeIfNeeded();

        this.str.append( '<' );
        this.str.append( name );

        this.openTags.push( name );
        this.addedInner = false;
        this.closedEmptyTag = false;

        return this;
    }

    public HtmlBuilder close()
    {
        this.str.append( "</" );
        this.str.append( this.openTags.pop() );
        this.str.append( '>' );
        return this;
    }

    public HtmlBuilder closeEmpty()
    {
        this.str.append( "/>" );
        this.openTags.pop();
        this.closedEmptyTag = true;

        return this;
    }

    public HtmlBuilder attribute( final String name, final String value )
    {
        this.str.append( ' ' );
        this.str.append( escaper.escape( name ) );
        this.str.append( "=\"" );
        this.str.append( escaper.escape( value ) );
        this.str.append( '"' );
        this.addedInner = false;
        this.closedEmptyTag = false;
        return this;
    }

    public HtmlBuilder escapedText( final String text )
    {
        return text( this.escaper.escape( text ) );
    }

    public HtmlBuilder text( final String text )
    {
        closeIfNeeded();
        this.str.append( text );
        this.addedInner = true;
        this.closedEmptyTag = false;
        return this;
    }

    @Override
    public String toString()
    {
        return this.str.toString();
    }
}
