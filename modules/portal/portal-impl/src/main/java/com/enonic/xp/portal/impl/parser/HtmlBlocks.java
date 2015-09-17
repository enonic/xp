package com.enonic.xp.portal.impl.parser;

import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.support.AbstractImmutableEntityList;

public final class HtmlBlocks
    extends AbstractImmutableEntityList<HtmlBlock>
{
    private String html;

    private final boolean hasInstruction;

    private final boolean hasTagMarker;

    private HtmlBlocks( final ImmutableList<HtmlBlock> list, final boolean hasInstruction, final boolean hasTagMarker )
    {
        super( list );
        this.hasInstruction = hasInstruction;
        this.hasTagMarker = hasTagMarker;
    }

    public static HtmlBlocks.Builder builder()
    {
        return new Builder();
    }

    public boolean hasInstructions()
    {
        return this.hasInstruction;
    }

    public boolean hasTagMarkers()
    {
        return this.hasTagMarker;
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

    public static class Builder
    {
        private ImmutableList.Builder<HtmlBlock> htmlBlocks = ImmutableList.builder();

        private boolean hasInstruction = false;

        private boolean hasTagMarker = false;

        public Builder add( final HtmlBlock value )
        {
            if ( value instanceof Instruction )
            {
                hasInstruction = true;
            }
            else if ( value instanceof TagMarker )
            {
                hasTagMarker = true;
            }
            htmlBlocks.add( value );
            return this;
        }

        public Builder addAll( final Iterable<HtmlBlock> values )
        {
            for ( HtmlBlock htmlBlock : values )
            {
                if ( hasInstruction && hasTagMarker )
                {
                    break;
                }

                if ( htmlBlock instanceof Instruction )
                {
                    hasInstruction = true;
                }
                else if ( htmlBlock instanceof TagMarker )
                {
                    hasTagMarker = true;
                }
            }

            htmlBlocks.addAll( values );
            return this;
        }

        public HtmlBlocks build()
        {
            return new HtmlBlocks( htmlBlocks.build(), hasInstruction, hasTagMarker );
        }
    }

}
