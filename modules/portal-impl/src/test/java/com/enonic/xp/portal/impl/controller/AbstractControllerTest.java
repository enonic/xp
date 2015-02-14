package com.enonic.xp.portal.impl.controller;

import org.junit.Before;
import org.mockito.Mockito;

import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceUrlTestHelper;
import com.enonic.xp.portal.PortalContext;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.postprocess.PostProcessor;
import com.enonic.xp.portal.impl.script.ScriptServiceImpl;

public abstract class AbstractControllerTest
{
    private ControllerScript controllerScript;

    protected PostProcessor postProcessor;

    private ControllerScriptFactoryImpl factory;

    protected PortalContext context;

    protected PortalResponse response;

    @Before
    public void setup()
        throws Exception
    {
        ResourceUrlTestHelper.mockModuleScheme().modulesClassLoader( getClass().getClassLoader() );

        this.context = new PortalContext();
        this.response = this.context.getResponse();

        this.factory = new ControllerScriptFactoryImpl();
        this.factory.setScriptService( new ScriptServiceImpl() );

        this.postProcessor = Mockito.mock( PostProcessor.class );
        this.factory.setPostProcessor( this.postProcessor );

        final ResourceKey scriptDir = ResourceKey.from( "mymodule:/service/test" );
        this.controllerScript = factory.newController( scriptDir );
    }

    protected final void execute( final String scriptDir )
    {
        this.controllerScript = this.factory.newController( ResourceKey.from( scriptDir ) );
        this.controllerScript.execute( this.context );
    }
}
