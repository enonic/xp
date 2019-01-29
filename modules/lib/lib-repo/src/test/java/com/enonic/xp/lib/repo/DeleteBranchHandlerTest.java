package com.enonic.xp.lib.repo;

import org.junit.Test;

import com.enonic.xp.repository.RepositoryExeption;

public class DeleteBranchHandlerTest
{
    @Test(expected = RepositoryExeption.class)
    public void protected_system_repo()
        throws Exception
    {
        final DeleteBranchHandler handler = new DeleteBranchHandler();
        handler.setRepoId( "system-repo" );
        handler.setBranchId( "master" );
        handler.execute();
    }

    @Test(expected = RepositoryExeption.class)
    public void protected_cms_repo_draft()
        throws Exception
    {
        final DeleteBranchHandler handler = new DeleteBranchHandler();
        handler.setRepoId( "com.enonic.cms.default" );
        handler.setBranchId( "draft" );
        handler.execute();
    }

    @Test(expected = RepositoryExeption.class)
    public void protected_cms_repo_master()
        throws Exception
    {
        final DeleteBranchHandler handler = new DeleteBranchHandler();
        handler.setRepoId( "com.enonic.cms.default" );
        handler.setBranchId( "master" );
        handler.execute();
    }
}