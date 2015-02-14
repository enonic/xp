package com.enonic.xp.portal.impl.jslib;

import org.junit.Before;

import junit.framework.Assert;

import com.enonic.wem.api.branch.Branch;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.module.ModuleKey;
import com.enonic.xp.portal.impl.script.AbstractScriptTest;
import com.enonic.xp.portal.script.ScriptExports;
import com.enonic.xp.portal.script.command.CommandHandler;
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
        this.context = new PortalContext();
        this.context.setMode( RenderMode.LIVE );
        this.context.setBranch( Branch.from( "draft" ) );
        this.context.setModule( ModuleKey.from( "mymodule" ) );
        this.context.setBaseUri( "/portal" );

        final Content content = Content.newContent().id( ContentId.from( "123" ) ).path( "some/path" ).build();
        this.context.setContent( content );
        PortalContextAccessor.set( this.context );

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
