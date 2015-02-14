package com.enonic.xp.portal.url;

import org.junit.Before;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.module.ModuleKey;
import com.enonic.xp.portal.PortalContext;

public abstract class AbstractUrlParamsTest
{
    protected PortalContext context;

    @Before
    public void setup()
    {
        this.context = new PortalContext();
        this.context.setBranch( Branch.from( "draft" ) );
        this.context.setModule( ModuleKey.from( "mymodule" ) );
        this.context.setBaseUri( "/portal" );
        this.context.setContentPath( ContentPath.from( "context/path" ) );
    }

    protected final <T extends AbstractUrlParams> T configure( final T params )
    {
        params.context( this.context );
        return params;
    }
}
