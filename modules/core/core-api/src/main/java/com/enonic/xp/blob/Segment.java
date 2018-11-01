package com.enonic.xp.blob;

import java.util.Arrays;
import java.util.List;

import com.google.common.collect.ImmutableList;

public final class Segment
{
    public static final Segment[] DEFAULT_REQUIRED_SEGMENTS = new Segment[]{Segment.from( "node" ), Segment.from( "binary" )};

    private final ImmutableList<SegmentLevel> levels;

    private Segment( final ImmutableList<SegmentLevel> levels )
    {
        this.levels = levels;
    }

    public static Segment from( final String... levels )
    {
        final ImmutableList.Builder<SegmentLevel> builder = ImmutableList.builder();
        Arrays.stream( levels ).
            forEach( level -> builder.add( SegmentLevel.from( level ) ) );
        return new Segment( builder.build() );
    }

    public static Segment from( final SegmentLevel... levels )
    {
        return new Segment( ImmutableList.copyOf( levels ) );
    }

    public static Segment from( final Iterable<SegmentLevel> levels )
    {
        return new Segment( ImmutableList.copyOf( levels ) );
    }

    public List<SegmentLevel> getLevels()
    {
        return levels;
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
}
