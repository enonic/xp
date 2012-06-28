/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.wem.core.jcr;

import java.util.List;

public final class PageList<E>
{
    private final int index;

    private final int total;

    private final List<E> list;

    public PageList( int index, int total, List<E> list )
    {
        this.index = index;
        this.total = total;
        this.list = list;
    }

    public int getIndex()
    {
        return index;
    }

    public int getTotal()
    {
        return total;
    }

    public List<E> getList()
    {
        return list;
    }
}
