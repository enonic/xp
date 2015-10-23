package com.enonic.xp.repo.impl.storage;

import java.util.Iterator;
import java.util.List;

import com.google.common.collect.Lists;

public class GetResults
    implements Iterable<GetResult>
{
    private final List<GetResult> getResults = Lists.newLinkedList();

    public void add( final GetResult getResult )
    {
        this.getResults.add( getResult );
    }

    @Override
    public Iterator<GetResult> iterator()
    {
        return this.getResults.iterator();
    }
}

