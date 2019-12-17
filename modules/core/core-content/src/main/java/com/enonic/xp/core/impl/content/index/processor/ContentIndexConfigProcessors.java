package com.enonic.xp.core.impl.content.index.processor;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class ContentIndexConfigProcessors
    implements Iterable<ContentIndexConfigProcessor>
{
    private final Set<ContentIndexConfigProcessor> processors;

    public ContentIndexConfigProcessors()
    {
        this.processors = new HashSet<>();
    }

    @Override
    public Iterator<ContentIndexConfigProcessor> iterator()
    {
        return this.processors.iterator();
    }

    public ContentIndexConfigProcessors add( final ContentIndexConfigProcessor processor )
    {
        this.processors.add( processor );
        return this;
    }
}
