package com.enonic.xp.blob;

import java.util.Arrays;
import java.util.List;

import com.google.common.collect.ImmutableList;

public final class Segment
{
    public static final int SEGMENT_LEVEL_DEPTH = 2;

    private final ImmutableList<SegmentLevel> levels;

    private Segment( final Builder builder )
    {
        final ImmutableList<SegmentLevel> levels = builder.levels.build();
        if ( levels.size() != SEGMENT_LEVEL_DEPTH )
        {
            throw new IllegalArgumentException( "Segment must have " + SEGMENT_LEVEL_DEPTH + " levels" );
        }
        this.levels = builder.levels.build();
    }

    public List<SegmentLevel> getLevels()
    {
        return levels;
    }

    public SegmentLevel getLevel(final int levelIndex)
    {
        return levels.get( levelIndex );
    }

    @Override
    public String toString()
    {
        return levels.toString();
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

        final Segment segment = (Segment) o;

        return levels != null ? levels.equals( segment.levels ) : segment.levels == null;

    }

    @Override
    public int hashCode()
    {
        return levels != null ? levels.hashCode() : 0;
    }

    public static Segment from( final String... levels )
    {
        return create().
            levels( levels ).
            build();
    }

    public static Segment from( final SegmentLevel... levels )
    {
        return create().
            levels( levels ).
            build();
    }

    public static Segment from( final Iterable<SegmentLevel> levels )
    {
        return create().
            levels( levels ).
            build();
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
    {
        private ImmutableList.Builder<SegmentLevel> levels = ImmutableList.builder();

        public Builder level( SegmentLevel level )
        {
            this.levels.add( level );
            return this;
        }

        public Builder level( String level )
        {
            this.levels.add( SegmentLevel.from( level ) );
            return this;
        }

        public Builder levels( SegmentLevel... levels )
        {
            Arrays.stream( levels ).
                forEach( this.levels::add );
            return this;
        }

        public Builder levels( String... levels )
        {
            Arrays.stream( levels ).
                map( SegmentLevel::from ).
                forEach( this.levels::add );
            return this;
        }

        public Builder levels( Iterable<SegmentLevel> levels )
        {
            this.levels.addAll( levels );
            return this;
        }

        public Segment build()
        {
            return new Segment( this );
        }
    }
}
