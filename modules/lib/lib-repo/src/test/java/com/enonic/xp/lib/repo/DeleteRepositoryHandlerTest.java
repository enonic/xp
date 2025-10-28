package com.enonic.xp.lib.repo;

import org.junit.jupiter.api.Test;

import com.enonic.xp.repository.RepositoryExeption;
import com.enonic.xp.repository.RepositoryService;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.testing.ScriptTestSupport;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

class DeleteRepositoryHandlerTest
    extends ScriptTestSupport
{
    @Override
    protected void initialize()
        throws Exception
    {
        super.initialize();

        addService( RepositoryService.class, mock( RepositoryService.class ) );
    }

    @Test
    void protected_system_repo()
    {
        final DeleteRepositoryHandler handler = new DeleteRepositoryHandler();
        handler.setRepositoryId( "system-repo" );
        assertThrows( RepositoryExeption.class, handler::execute );
    }

    @Test
    void protected_cms_repo()
    {
        final DeleteRepositoryHandler handler = new DeleteRepositoryHandler();
        handler.initialize( newBeanContext( ResourceKey.from( "myapp:/test" ) ) );
        handler.setRepositoryId( "com.enonic.cms.default" );
        assertDoesNotThrow( handler::execute );
    }
}
