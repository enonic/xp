package com.enonic.wem.repo.internal.cache;

import org.junit.Before;
import org.junit.Test;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.node.NodePath;

import static org.junit.Assert.*;

public class PathCacheImplTest
{
    @Before
    public void setUp()
        throws Exception
    {

    }

    @Test
    public void put()
        throws Exception
    {
        final PathCacheImpl cache = new PathCacheImpl();

        final CachePath a = createPath( "a" );

        cache.cache( a, "1" );
        assertEquals( "1", cache.get( a ) );
    }

    @Test
    public void remove()
        throws Exception
    {
        final PathCacheImpl cache = new PathCacheImpl();

        final CachePath a = createPath( "a" );

        cache.cache( a, "1" );
        cache.evict( a );
        assertNull( cache.get( a ) );
    }

    private CachePath createPath( final String path )
    {
        return new BranchPath( Branch.from( "test" ), NodePath.create( path ).build() );
    }

}