package com.enonic.xp.lib.repo;

import org.mockito.Mockito;

import com.enonic.xp.repository.RepositoryService;
import com.enonic.xp.testing.script.ScriptTestSupport;

public class CreateRepositoryScriptTest
    extends ScriptTestSupport
{
    private RepositoryService repositoryService;

    @Override
    public void initialize()
        throws Exception
    {
        super.initialize();
        repositoryService = Mockito.mock( RepositoryService.class );
        Mockito.when( repositoryService.create( Mockito.any() ) ).
            thenAnswer( invocation -> invocation.getArguments()[0] );
        addService( RepositoryService.class, repositoryService );
    }

//    @Test
//    public void testExample()
//    {
//        runScript( "/site/lib/xp/examples/repo/create.js" );
//        verify( this.repositoryService, times( 2 ) ).create( Mockito.any() );
//    }
}
