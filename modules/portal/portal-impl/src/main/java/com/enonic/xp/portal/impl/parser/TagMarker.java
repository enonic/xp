package com.enonic.xp.portal.impl.parser;

import com.google.common.base.MoreObjects;

import com.enonic.xp.portal.postprocess.HtmlTag;

public final class TagMarker
    extends HtmlBlock
{
    private final HtmlTag htmlTag;

    public TagMarker( final HtmlTag htmlTag )
    {
        this.htmlTag = htmlTag;
    }

    public HtmlTag getTag()
    {
        return htmlTag;
    }

    @Override
    public String getHtml()
    {
        return "";
    }

    @Override
    public String toString()
    {
        final MoreObjects.ToStringHelper s = MoreObjects.toStringHelper( this );
        s.add( "tag", htmlTag );
        return s.toString();
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }

        final TagMarker tagMarker = (TagMarker) o;
        return htmlTag == tagMarker.htmlTag;
    }

    @Override
    public int hashCode()
    {
        return htmlTag.hashCode();
    }
}
