package com.enonic.wem.portal.internal.controller;

import org.junit.Before;
import org.mockito.Mockito;

import com.enonic.wem.api.resource.ResourceKey;
import com.enonic.wem.api.resource.ResourceUrlTestHelper;
import com.enonic.wem.portal.PortalResponse;
import com.enonic.wem.portal.internal.postprocess.PostProcessor;
import com.enonic.wem.script.internal.ScriptServiceImpl;

public abstract class AbstractControllerTest
{
    private ControllerScript controllerScript;

    protected PostProcessor postProcessor;

    private ControllerScriptFactoryImpl factory;

    protected PortalContextImpl context;

    protected PortalRequestImpl request;

    protected PortalResponse response;

    @Before
    public void setup()
        throws Exception
    {
        ResourceUrlTestHelper.mockModuleScheme().modulesClassLoader( getClass().getClassLoader() );

        this.context = new PortalContextImpl();
        this.response = this.context.getResponse();

        this.factory = new ControllerScriptFactoryImpl();
        this.factory.setScriptService( new ScriptServiceImpl() );

        this.postProcessor = Mockito.mock( PostProcessor.class );
        this.factory.setPostProcessor( this.postProcessor );

        final ResourceKey scriptDir = ResourceKey.from( "mymodule:/service/test" );
        this.controllerScript = factory.newController( scriptDir );

        this.request = new PortalRequestImpl();
        this.context.setRequest( this.request );
    }

    protected final void execute( final String scriptDir )
    {
        this.controllerScript = this.factory.newController( ResourceKey.from( scriptDir ) );
        this.controllerScript.execute( this.context );
    }
}
