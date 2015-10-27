package com.enonic.xp.repo.impl.cache;

import org.junit.Test;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.node.NodePath;

import static org.junit.Assert.*;

public class PathCacheImplTest
{

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


    @Test
    public void update_entry()
        throws Exception
    {
        final PathCacheImpl cache = new PathCacheImpl();

        cache.cache( createPath( "/oldPath" ), "1" );
        cache.cache( createPath( "/newPath" ), "1" );

        assertEquals( "1", cache.get( createPath( "/newPath" ) ) );
    }

    @Test
    public void update_entry_2()
        throws Exception
    {
        final PathCacheImpl cache = new PathCacheImpl();

        cache.cache( createPath( "/oldPath" ), "1" );
        cache.cache( createPath( "/oldPath" ), "2" );
        cache.cache( createPath( "/newPath" ), "1" );

        assertEquals( "2", cache.get( createPath( "/oldPath" ) ) );
        assertEquals( "1", cache.get( createPath( "/newPath" ) ) );
    }


    private CachePath createPath( final String path )
    {
        return new BranchPath( Branch.from( "test" ), NodePath.create( path ).build() );
    }

}