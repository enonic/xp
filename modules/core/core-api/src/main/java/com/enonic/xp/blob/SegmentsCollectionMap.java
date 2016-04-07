package com.enonic.xp.blob;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

public class SegmentsCollectionMap
    implements Iterable<String>
{
    private final Map<Segment, String> segmentsMap;

    public SegmentsCollectionMap( final Map<Segment, String> segmentsMap )
    {
        this.segmentsMap = segmentsMap;
    }

    public String get( final Segment segment )
    {
        return this.segmentsMap.get( segment );
    }

    public Collection<String> getValues()
    {
        return this.segmentsMap.values();
    }

    @Override
    public Iterator<String> iterator()
    {
        return this.segmentsMap.values().iterator();
    }
}
