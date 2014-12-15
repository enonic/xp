package com.enonic.wem.portal.internal.command;

import org.junit.Before;
import org.mockito.Mockito;

import junit.framework.Assert;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.workspace.Workspace;
import com.enonic.wem.portal.PortalContextAccessor;
import com.enonic.wem.portal.PortalRequest;
import com.enonic.wem.portal.RenderMode;
import com.enonic.wem.portal.internal.controller.PortalContextImpl;
import com.enonic.wem.script.AbstractScriptTest;
import com.enonic.wem.script.ScriptExports;
import com.enonic.wem.script.command.CommandHandler;

public abstract class AbstractUrlHandlerTest
    extends AbstractScriptTest
{
    @Before
    public final void setup()
        throws Exception
    {
        final PortalRequest request = Mockito.mock( PortalRequest.class );
        Mockito.when( request.getBaseUri() ).thenReturn( "/root" );
        Mockito.when( request.getMode() ).thenReturn( RenderMode.LIVE );
        Mockito.when( request.getWorkspace() ).thenReturn( Workspace.from( "stage" ) );

        final PortalContextImpl context = new PortalContextImpl();
        context.setRequest( request );
        context.setModule( ModuleKey.from( "mymodule" ) );

        final Content content = Content.newContent().id( ContentId.from( "123" ) ).path( "some/path" ).build();
        context.setContent( content );

        PortalContextAccessor.set( context );
        addHandler( createHandler() );
    }

    protected abstract CommandHandler createHandler()
        throws Exception;

    protected void execute( final String name )
        throws Exception
    {
        final String path = getClass().getName().replace( '.', '/' ) + ".js";
        final ScriptExports exports = runTestScript( path );

        Assert.assertTrue( "No functions exported named [" + name + "]", exports.hasMethod( name ) );
        exports.executeMethod( name );
    }
}
