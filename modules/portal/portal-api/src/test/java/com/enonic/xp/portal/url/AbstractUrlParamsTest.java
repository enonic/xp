package com.enonic.xp.portal.url;

import org.junit.jupiter.api.BeforeEach;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.portal.PortalRequest;

public abstract class AbstractUrlParamsTest
{
    protected PortalRequest portalRequest;

    @BeforeEach
    public void setup()
    {
        this.portalRequest = new PortalRequest();
        this.portalRequest.setBranch( Branch.from( "draft" ) );
        this.portalRequest.setApplicationKey( ApplicationKey.from( "myapplication" ) );
        this.portalRequest.setBaseUri( "/site" );
        this.portalRequest.setContentPath( ContentPath.from( "context/path" ) );
    }

    protected final <T extends AbstractUrlParams> T configure( final T params )
    {
        params.portalRequest( this.portalRequest );
        return params;
    }
}
