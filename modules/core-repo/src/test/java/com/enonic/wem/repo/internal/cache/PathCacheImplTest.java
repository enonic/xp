package com.enonic.wem.repo.internal.cache;

import java.util.Collection;

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
    public void children()
        throws Exception
    {
        final PathCacheImpl cache = new PathCacheImpl();

        cache.cache( createPath( "a" ), "1" );
        cache.cache( createPath( "a/b" ), "2" );

        final Collection<String> children = cache.getChildren( createPath( "a" ) );
        assertEquals( 1, children.size() );
        assertEquals( "2", children.iterator().next() );
    }

    @Test
    public void multiple_children()
        throws Exception
    {
        final PathCacheImpl cache = new PathCacheImpl();

        cache.cache( createPath( "a" ), "1" );
        cache.cache( createPath( "a/b" ), "4" );
        cache.cache( createPath( "a/d" ), "2" );
        cache.cache( createPath( "a/c" ), "3" );
        cache.cache( createPath( "a/a" ), "5" );

        final Collection<String> children = cache.getChildren( createPath( "a" ) );
        assertEquals( 4, children.size() );
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

        cache.cache( createPath( "a" ), "2" );

        cache.cache( createPath( "a/b" ), "2" );

        cache.cache( createPath( "b" ), "2" );

        assertTrue( cache.getChildren( createPath( "a" ) ).isEmpty() );
    }

    private CachePath createPath( final String path )
    {
        return new BranchPath( Branch.from( "test" ), NodePath.create( path ).build() );
    }

}