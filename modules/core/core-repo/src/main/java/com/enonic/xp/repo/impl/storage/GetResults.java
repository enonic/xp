package com.enonic.xp.repo.impl.storage;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GetResults
    implements Iterable<GetResult>
{
    private final List<GetResult> getResults = new ArrayList<>();

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

