package com.enonic.xp.repo.impl.cache;

import org.junit.Test;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.repo.impl.branch.storage.BranchDocumentId;

import static org.junit.Assert.*;

public class PathCacheImplTest
{

    @Test
    public void put()
        throws Exception
    {
        final PathCacheImpl cache = new PathCacheImpl();

        final CachePath a = createPath( "a" );

        cache.cache( a, BranchDocumentId.from( "_1_draft" ) );
        assertEquals( "_1_draft", cache.get( a ) );
    }

    @Test
    public void remove()
        throws Exception
    {
        final PathCacheImpl cache = new PathCacheImpl();

        final CachePath a = createPath( "a" );

        cache.cache( a, BranchDocumentId.from( "1_draft" ) );
        cache.evict( a );
        assertNull( cache.get( a ) );
    }


    @Test
    public void update_entry()
        throws Exception
    {
        final PathCacheImpl cache = new PathCacheImpl();

        cache.cache( createPath( "/oldPath" ), BranchDocumentId.from( "1_draft" ) );
        cache.cache( createPath( "/newPath" ), BranchDocumentId.from( "1_draft" ) );

        assertEquals( "1_draft", cache.get( createPath( "/newPath" ) ) );
    }

    @Test
    public void update_entry_2()
        throws Exception
    {
        final PathCacheImpl cache = new PathCacheImpl();

        cache.cache( createPath( "/oldPath" ), BranchDocumentId.from( "1_draft" ) );
        cache.cache( createPath( "/oldPath" ), BranchDocumentId.from( "2_draft" ) );
        cache.cache( createPath( "/newPath" ), BranchDocumentId.from( "1_draft" ) );

        assertEquals( "2_draft", cache.get( createPath( "/oldPath" ) ) );
        assertEquals( "1_draft", cache.get( createPath( "/newPath" ) ) );
    }


    private CachePath createPath( final String path )
    {
        return new BranchPath( Branch.from( "test" ), NodePath.create( path ).build() );
    }

}