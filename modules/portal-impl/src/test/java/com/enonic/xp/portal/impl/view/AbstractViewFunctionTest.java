package com.enonic.xp.portal.impl.view;

import org.junit.Before;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.module.ModuleKey;
import com.enonic.xp.portal.PortalContext;
import com.enonic.xp.portal.view.ViewFunction;
import com.enonic.xp.portal.view.ViewFunctionParams;

public abstract class AbstractViewFunctionTest
{
    protected ViewFunctionServiceImpl service;

    protected PortalContext context;

    @Before
    public final void setup()
        throws Exception
    {
        this.context = new PortalContext();
        this.context.setBranch( Branch.from( "draft" ) );
        this.context.setModule( ModuleKey.from( "mymodule" ) );
        this.context.setBaseUri( "/portal" );
        this.context.setContentPath( ContentPath.from( "context/path" ) );

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
        final ViewFunctionParams params = new ViewFunctionParams().context( this.context ).name( name ).args( args );
        return this.service.execute( params );
    }
}
