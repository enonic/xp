package com.enonic.xp.repo.impl.cache;

import org.junit.Test;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.repo.impl.branch.storage.BranchDocumentId;
import com.enonic.xp.repository.RepositoryId;

import static org.junit.Assert.*;

public class BranchCachePathTest
{
    @Test
    public void put()
        throws Exception
    {
        final BranchCachePath cache = new BranchCachePath();

        final CachePath a = createPath( "a" );

        final BranchDocumentId documentId = BranchDocumentId.from( "_1_draft" );
        cache.cache( a, documentId );
        assertEquals( documentId, cache.get( a ) );
    }

    @Test
    public void remove()
        throws Exception
    {
        final BranchCachePath cache = new BranchCachePath();

        final CachePath a = createPath( "a" );

        final BranchDocumentId documentId = BranchDocumentId.from( "1_draft" );
        cache.cache( a, documentId );
        cache.evict( a );
        assertNull( cache.get( a ) );
    }

    @Test
    public void update_entry()
        throws Exception
    {
        final BranchCachePath cache = new BranchCachePath();

        final BranchDocumentId documentId = BranchDocumentId.from( "1_draft" );
        cache.cache( createPath( "/oldPath" ), documentId );
        cache.cache( createPath( "/newPath" ), documentId );

        assertEquals( documentId, cache.get( createPath( "/newPath" ) ) );
    }

    @Test
    public void update_entry_2()
        throws Exception
    {
        final BranchCachePath cache = new BranchCachePath();

        final BranchDocumentId id1 = BranchDocumentId.from( "1_draft" );
        final BranchDocumentId id2 = BranchDocumentId.from( "2_draft" );
        cache.cache( createPath( "/oldPath" ), id1 );
        cache.cache( createPath( "/oldPath" ), id2 );
        cache.cache( createPath( "/newPath" ), id1 );

        assertEquals( id2, cache.get( createPath( "/oldPath" ) ) );
        assertEquals( id1, cache.get( createPath( "/newPath" ) ) );
    }

    @Test
    public void repo_separation()
        throws Exception
    {
        final BranchCachePath cache = new BranchCachePath();

        final CachePath repo1Path = createPath( "/myPath", RepositoryId.from( "repo1" ) );
        final CachePath repo2Path = createPath( "/myPath", RepositoryId.from( "repo2" ) );
        final BranchDocumentId id1 = BranchDocumentId.from( "1_fisk" );
        final BranchDocumentId id2 = BranchDocumentId.from( "2_fisk" );
        cache.cache( repo1Path, id1 );
        cache.cache( repo2Path, id2 );

        assertEquals( id1, cache.get( repo1Path ) );
        assertEquals( id2, cache.get( repo2Path ) );
    }

    private CachePath createPath( final String path )
    {
        final RepositoryId repo = RepositoryId.from( "repo" );
        return createPath( path, repo );
    }

    private CachePath createPath( final String path, final RepositoryId repo )
    {
        return new BranchPath( repo, Branch.from( "test" ), NodePath.create( path ).build() );
    }

}