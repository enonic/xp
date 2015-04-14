package com.enonic.xp.portal.impl.parser;

import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.support.AbstractImmutableEntityList;

public final class HtmlBlocks
    extends AbstractImmutableEntityList<HtmlBlock>
{
    private String html;

    private HtmlBlocks( final ImmutableList<HtmlBlock> list )
    {
        super( list );
    }

    public static HtmlBlocks from( final Iterable<? extends HtmlBlock> htmlBlocks )
    {
        return new HtmlBlocks( ImmutableList.copyOf( htmlBlocks ) );
    }

    @Override
    public String toString()
    {
        if ( this.html == null )
        {
            this.html = this.stream().map( HtmlBlock::getHtml ).collect( Collectors.joining() );
        }
        return this.html;
    }

}
