package com.enonic.xp.lib.repo;

import org.junit.Test;

import com.enonic.xp.repository.RepositoryExeption;

public class DeleteRepositoryHandlerTest
{
    @Test(expected = RepositoryExeption.class)
    public void name()
        throws Exception
    {
        final DeleteRepositoryHandler handler = new DeleteRepositoryHandler();
        handler.setRepositoryId( "system-repo" );
        handler.execute();
    }
}