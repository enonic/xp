package com.enonic.xp.core.impl.content.processor;

import java.util.Iterator;
import java.util.Set;

import com.google.common.collect.Sets;

public class ContentProcessors
    implements Iterable<ContentProcessor>
{
    private final Set<ContentProcessor> processors;

    public ContentProcessors()
    {
        this.processors = Sets.newHashSet();
    }

    @Override
    public Iterator<ContentProcessor> iterator()
    {
        return this.processors.iterator();
    }

    public void add( final ContentProcessor processor )
    {
        this.processors.add( processor );
    }

    public void remove( final ContentProcessor processor )
    {
        this.processors.remove( processor );
    }
}
