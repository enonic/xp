package com.enonic.wem.admin.rest.resource.content.site;

import com.enonic.wem.admin.json.site.SiteJson;
import com.enonic.wem.admin.rest.resource.ErrorJson;
import com.enonic.wem.api.content.site.Site;

public class CreateSiteResult
{
    private final ErrorJson error;

    private final SiteJson site;

    private CreateSiteResult( final ErrorJson error, final SiteJson site )
    {
        this.error = error;
        this.site = site;
    }

    public ErrorJson getError()
    {
        return error;
    }

    public SiteJson getSite()
    {
        return site;
    }

    public static CreateSiteResult success( Site site )
    {
        return new CreateSiteResult( null, new SiteJson( site ) );
    }

    public static CreateSiteResult error( String message )
    {
        return new CreateSiteResult( new ErrorJson( message ), null );
    }
}
