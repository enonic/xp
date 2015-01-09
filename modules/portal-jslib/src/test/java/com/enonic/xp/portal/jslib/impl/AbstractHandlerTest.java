package com.enonic.xp.portal.jslib.impl;

import org.junit.Before;
import org.mockito.Mockito;

import junit.framework.Assert;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.workspace.Workspace;
import com.enonic.wem.script.AbstractScriptTest;
import com.enonic.wem.script.ScriptExports;
import com.enonic.wem.script.command.CommandHandler;
import com.enonic.xp.portal.PortalContext;
import com.enonic.xp.portal.PortalContextAccessor;
import com.enonic.xp.portal.RenderMode;

public abstract class AbstractHandlerTest
    extends AbstractScriptTest
{
    protected PortalContext context;

    @Before
    public final void setup()
        throws Exception
    {
        context = Mockito.mock( PortalContext.class );
        Mockito.when( context.getMode() ).thenReturn( RenderMode.LIVE );
        Mockito.when( context.getWorkspace() ).thenReturn( Workspace.from( "stage" ) );
        Mockito.when( context.getModule() ).thenReturn( ModuleKey.from( "mymodule" ) );

        final Content content = Content.newContent().id( ContentId.from( "123" ) ).path( "some/path" ).build();
        Mockito.when( context.getContent() ).thenReturn( content );
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
