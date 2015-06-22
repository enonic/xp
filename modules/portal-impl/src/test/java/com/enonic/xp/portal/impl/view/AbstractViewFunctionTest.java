package com.enonic.xp.portal.impl.view;

import org.junit.Before;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.module.ModuleKey;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.view.ViewFunction;
import com.enonic.xp.portal.view.ViewFunctionParams;

public abstract class AbstractViewFunctionTest
{
    protected ViewFunctionServiceImpl service;

    protected PortalRequest portalRequest;

    @Before
    public final void setup()
        throws Exception
    {
        this.portalRequest = new PortalRequest();
        this.portalRequest.setBranch( Branch.from( "draft" ) );
        this.portalRequest.setModule( ModuleKey.from( "mymodule" ) );
        this.portalRequest.setBaseUri( "/portal" );
        this.portalRequest.setContentPath( ContentPath.from( "context/path" ) );

        this.service = new ViewFunctionServiceImpl();
        setupFunction();
    }

    protected final void register( final ViewFunction function )
    {
        this.service.addFunction( function );
    }

    protected abstract void setupFunction()
        throws Exception;

    protected final Object execute( final String name, final String... args )
    {
        final ViewFunctionParams params = new ViewFunctionParams().portalRequest( this.portalRequest ).name( name ).args( args );
        return this.service.execute( params );
    }
}
