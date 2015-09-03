package com.enonic.wem.repo.internal.storage;

import org.junit.Before;
import org.junit.Test;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.node.NodePath;

import static org.junit.Assert.*;

public class SimpleCacheTest
{

    private SimpleCache cache;

    @Before
    public void setUp()
        throws Exception
    {
        cache = new SimpleCache();
    }

    @Test
    public void delete()
        throws Exception
    {
        final StorageData data = StorageData.create().
            add( "path", "/oldPath" ).
            build();

        final BranchPathCacheKey cacheKey = new BranchPathCacheKey( Branch.from( "myBranch" ), NodePath.create( "oldPath" ).build() );

        cache.put( CacheStoreRequest.create().
            id( "myId" ).
            storageData( data ).
            addCacheKey( cacheKey ).
            build() );

        CacheResult cacheResult = cache.get( cacheKey );

        assertTrue( cacheResult.exists() );

        cache.evict( "myId" );

        cacheResult = cache.get( cacheKey );

        assertFalse( cacheResult.exists() );
    }

    @Test
    public void update_cached_entry()
        throws Exception
    {
        cache.put( CacheStoreRequest.create().
            id( "myId" ).
            storageData( StorageData.create().
                add( "path", "/oldPath" ).
                build() ).
            addCacheKey( new BranchPathCacheKey( Branch.from( "myBranch" ), NodePath.create( "oldPath" ).build() ) ).
            build() );

        cache.put( CacheStoreRequest.create().
            id( "myId" ).
            storageData( StorageData.create().
                add( "path", "/newPath" ).
                build() ).
            addCacheKey( new BranchPathCacheKey( Branch.from( "myBranch" ), NodePath.create( "newPath" ).build() ) ).
            build() );

        assertFalse( cache.get( new BranchPathCacheKey( Branch.from( "myBranch" ), NodePath.create( "oldPath" ).build() ) ).exists() );
        assertTrue( cache.get( new BranchPathCacheKey( Branch.from( "myBranch" ), NodePath.create( "newPath" ).build() ) ).exists() );
    }
}