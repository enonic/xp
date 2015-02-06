package com.enonic.xp.portal.impl.url;

import org.junit.Before;
import org.mockito.Mockito;

import com.enonic.wem.api.branch.Branch;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.ContentService;
import com.enonic.wem.api.module.ModuleKey;
import com.enonic.xp.portal.PortalContext;

public abstract class AbstractPortalUrlServiceImplTest
{
    protected PortalContext context;

    protected PortalUrlServiceImpl service;

    protected ContentService contentService;

    @Before
    public void setup()
    {
        this.context = new PortalContext();
        this.context.setBranch( Branch.from( "draft" ) );
        this.context.setModule( ModuleKey.from( "mymodule" ) );
        this.context.setBaseUri( "/portal" );
        this.context.setContentPath( ContentPath.from( "context/path" ) );

        this.contentService = Mockito.mock( ContentService.class );
        this.service = new PortalUrlServiceImpl();
        this.service.setContentService( this.contentService );
    }
}
