package com.enonic.wem.core.search.indexing;

import java.util.List;

public interface BatchLoader<T>
{
    public int getTotal();

    public void setBatchSize( int size );

    public boolean hasNext();

    public List<T> next();

    public void reset();
}
