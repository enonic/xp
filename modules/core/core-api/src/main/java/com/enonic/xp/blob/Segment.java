package com.enonic.xp.blob;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class Segment
{
    public static final int SEGMENT_LEVEL_DEPTH = 2;

    private final List<SegmentLevel> levels;

    private Segment( final Builder builder )
    {
        if ( builder.levels.size() != SEGMENT_LEVEL_DEPTH )
        {
            throw new IllegalArgumentException( "Segment must have " + SEGMENT_LEVEL_DEPTH + " levels" );
        }

        this.levels = List.copyOf( builder.levels );
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
        return ( o instanceof Segment ) && this.levels.equals( ( (Segment) o ).levels );
    }

    @Override
    public int hashCode()
    {
        return levels.hashCode();
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

    public static final class Builder
    {
        private final ArrayList<SegmentLevel> levels = new ArrayList<>();

        private Builder()
        {
        }

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
            this.levels.addAll( Arrays.asList( levels ) );
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
            levels.forEach( this.levels::add );
            return this;
        }

        public Segment build()
        {
            return new Segment( this );
        }
    }
}
