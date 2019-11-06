package com.enonic.xp.core.impl.content.processor;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.enonic.xp.content.processor.ContentProcessor;

public class ContentProcessors
    implements Iterable<ContentProcessor>
{
    private final List<ContentProcessor> processors = new CopyOnWriteArrayList<>();

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
