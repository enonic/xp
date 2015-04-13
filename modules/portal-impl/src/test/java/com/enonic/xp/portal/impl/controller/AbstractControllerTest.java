package com.enonic.xp.portal.impl.controller;

import org.junit.Before;
import org.mockito.Mockito;

import com.enonic.xp.portal.PortalContext;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.impl.script.ScriptServiceImpl;
import com.enonic.xp.portal.postprocess.PostProcessor;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceUrlTestHelper;

public abstract class AbstractControllerTest
{
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
    }

    protected final void execute( final String scriptDir )
    {
        final ControllerScript controllerScript = this.factory.newController( ResourceKey.from( scriptDir ) );
        controllerScript.execute( this.context );
    }
}
