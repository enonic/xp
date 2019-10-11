package com.enonic.xp.web.vhost.impl.mapping;

import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;

public final class VirtualHostMappings
    implements Iterable<VirtualHostMapping>
{
    private final Set<VirtualHostMapping> set;

    public VirtualHostMappings()
    {
        this.set = new TreeSet<VirtualHostMapping>();
    }

    public void add( final VirtualHostMapping mapping )
    {
        this.set.add( mapping );
    }

    public VirtualHostMapping resolve( final HttpServletRequest req )
    {
        for ( final VirtualHostMapping entry : this.set )
        {
            if ( entry.matches( req ) )
            {
                return entry;
            }
        }

        return null;
    }

    @Override
    public Iterator<VirtualHostMapping> iterator()
    {
        return this.set.iterator();
    }
}
