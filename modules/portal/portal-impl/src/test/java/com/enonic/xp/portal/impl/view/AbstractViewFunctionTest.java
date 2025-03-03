package com.enonic.xp.portal.impl.view;

import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;

import jakarta.servlet.http.HttpServletRequest;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.view.ViewFunction;
import com.enonic.xp.portal.view.ViewFunctionParams;

public abstract class AbstractViewFunctionTest
{
    protected ViewFunctionServiceImpl service;

    protected PortalRequest portalRequest;

    @BeforeEach
    public final void setup()
        throws Exception
    {
        this.portalRequest = new PortalRequest();
        this.portalRequest.setBranch( Branch.from( "draft" ) );
        this.portalRequest.setApplicationKey( ApplicationKey.from( "myapplication" ) );
        this.portalRequest.setBaseUri( "/site" );
        this.portalRequest.setContentPath( ContentPath.from( "context/path" ) );

        HttpServletRequest httpServletRequest = Mockito.mock( HttpServletRequest.class );
        this.portalRequest.setRawRequest( httpServletRequest );

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
