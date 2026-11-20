package com.enonic.xp.core.impl.app.filter;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.util.NameFilter;

public class AppFilter
{
    private final NameFilter filter;

    public AppFilter( final String patterns )
    {
        this.filter = NameFilter.parse( patterns );
    }

    public boolean accept( ApplicationKey key )
    {
        return filter.accept( key.getName() );
    }
}
