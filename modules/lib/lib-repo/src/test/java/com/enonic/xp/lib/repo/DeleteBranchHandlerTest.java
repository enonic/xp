package com.enonic.xp.lib.repo;

import org.junit.jupiter.api.Test;

import com.enonic.xp.repository.RepositoryExeption;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class DeleteBranchHandlerTest
{
    @Test
    public void protected_system_repo()
        throws Exception
    {
        final DeleteBranchHandler handler = new DeleteBranchHandler();
        handler.setRepoId( "system-repo" );
        handler.setBranchId( "master" );
        assertThrows(RepositoryExeption.class, () -> handler.execute());
    }

    @Test
    public void protected_cms_repo_draft()
        throws Exception
    {
        final DeleteBranchHandler handler = new DeleteBranchHandler();
        handler.setRepoId( "com.enonic.cms.default" );
        handler.setBranchId( "draft" );
        assertThrows(RepositoryExeption.class, () -> handler.execute());
    }

    @Test
    public void protected_cms_repo_master()
        throws Exception
    {
        final DeleteBranchHandler handler = new DeleteBranchHandler();
        handler.setRepoId( "com.enonic.cms.default" );
        handler.setBranchId( "master" );
        assertThrows(RepositoryExeption.class, () -> handler.execute());
    }
}
