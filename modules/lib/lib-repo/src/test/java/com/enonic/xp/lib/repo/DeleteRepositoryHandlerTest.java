package com.enonic.xp.lib.repo;

import org.junit.jupiter.api.Test;

import com.enonic.xp.repository.RepositoryExeption;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class DeleteRepositoryHandlerTest
{
    @Test
    public void protected_system_repo()
        throws Exception
    {
        final DeleteRepositoryHandler handler = new DeleteRepositoryHandler();
        handler.setRepositoryId( "system-repo" );
        assertThrows(RepositoryExeption.class, () -> handler.execute());
    }

    @Test
    public void protected_cms_repo()
        throws Exception
    {
        final DeleteRepositoryHandler handler = new DeleteRepositoryHandler();
        handler.setRepositoryId( "com.enonic.cms.default" );
        assertThrows(RepositoryExeption.class, () -> handler.execute());
    }
}
