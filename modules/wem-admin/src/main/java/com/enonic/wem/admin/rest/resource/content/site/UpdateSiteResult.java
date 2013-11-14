package com.enonic.wem.admin.rest.resource.content.site;


import com.enonic.wem.admin.json.site.SiteJson;
import com.enonic.wem.admin.rest.resource.ErrorJson;
import com.enonic.wem.api.content.site.Site;

public class UpdateSiteResult
{
    private final ErrorJson error;

    private final SiteJson site;

    private UpdateSiteResult( final ErrorJson error, final SiteJson site )
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

    public static UpdateSiteResult success( Site site )
    {
        return new UpdateSiteResult( null, new SiteJson( site ) );
    }

    public static UpdateSiteResult error( String message )
    {
        return new UpdateSiteResult( new ErrorJson( message ), null );
    }
}
