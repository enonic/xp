package com.enonic.xp.lib.repo;

import org.junit.Test;

import com.enonic.xp.repository.RepositoryExeption;

public class DeleteRepositoryHandlerTest
{
    @Test(expected = RepositoryExeption.class)
    public void protected_system_repo()
        throws Exception
    {
        final DeleteRepositoryHandler handler = new DeleteRepositoryHandler();
        handler.setRepositoryId( "system-repo" );
        handler.execute();
    }

    @Test(expected = RepositoryExeption.class)
    public void protected_cms_repo()
        throws Exception
    {
        final DeleteRepositoryHandler handler = new DeleteRepositoryHandler();
        handler.setRepositoryId( "cms-repo" );
        handler.execute();
    }
}