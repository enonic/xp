package com.enonic.xp.portal.impl.url;

import org.junit.Before;
import org.mockito.Mockito;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.module.ModuleKey;
import com.enonic.xp.portal.PortalRequest;

public abstract class AbstractPortalUrlServiceImplTest
{
    protected PortalRequest portalRequest;

    protected PortalUrlServiceImpl service;

    protected ContentService contentService;

    @Before
    public void setup()
    {
        this.portalRequest = new PortalRequest();
        this.portalRequest.setBranch( Branch.from( "draft" ) );
        this.portalRequest.setModule( ModuleKey.from( "mymodule" ) );
        this.portalRequest.setBaseUri( "/portal" );
        this.portalRequest.setContentPath( ContentPath.from( "context/path" ) );

        this.contentService = Mockito.mock( ContentService.class );
        this.service = new PortalUrlServiceImpl();
        this.service.setContentService( this.contentService );
    }
}
