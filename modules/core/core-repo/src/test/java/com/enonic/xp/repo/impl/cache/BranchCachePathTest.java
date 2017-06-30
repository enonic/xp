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

        cache.cache( a, BranchDocumentId.from( "_1_draft" ) );
        assertEquals( "_1_draft", cache.get( a ) );
    }

    @Test
    public void remove()
        throws Exception
    {
        final BranchCachePath cache = new BranchCachePath();

        final CachePath a = createPath( "a" );

        cache.cache( a, BranchDocumentId.from( "1_draft" ) );
        cache.evict( a );
        assertNull( cache.get( a ) );
    }

    @Test
    public void update_entry()
        throws Exception
    {
        final BranchCachePath cache = new BranchCachePath();

        cache.cache( createPath( "/oldPath" ), BranchDocumentId.from( "1_draft" ) );
        cache.cache( createPath( "/newPath" ), BranchDocumentId.from( "1_draft" ) );

        assertEquals( "1_draft", cache.get( createPath( "/newPath" ) ) );
    }

    @Test
    public void update_entry_2()
        throws Exception
    {
        final BranchCachePath cache = new BranchCachePath();

        cache.cache( createPath( "/oldPath" ), BranchDocumentId.from( "1_draft" ) );
        cache.cache( createPath( "/oldPath" ), BranchDocumentId.from( "2_draft" ) );
        cache.cache( createPath( "/newPath" ), BranchDocumentId.from( "1_draft" ) );

        assertEquals( "2_draft", cache.get( createPath( "/oldPath" ) ) );
        assertEquals( "1_draft", cache.get( createPath( "/newPath" ) ) );
    }

    @Test
    public void repo_separation()
        throws Exception
    {
        final BranchCachePath cache = new BranchCachePath();

        final CachePath repo1Path = createPath( "/myPath", RepositoryId.from( "repo1" ) );
        final CachePath repo2Path = createPath( "/myPath", RepositoryId.from( "repo2" ) );
        cache.cache( repo1Path, BranchDocumentId.from( "1_fisk" ) );
        cache.cache( repo2Path, BranchDocumentId.from( "2_fisk" ) );

        assertEquals( "1_fisk", cache.get( repo1Path ) );
        assertEquals( "2_fisk", cache.get( repo2Path ) );
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