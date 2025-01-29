package com.enonic.xp.portal.impl.url3;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.impl.url2.ImageMediaUrlParams;
import com.enonic.xp.project.ProjectName;

public final class BaseUrlStrategyFactory
{

    private ContentService contentService;

    public static BaseUrlStrategy create( final ImageMediaUrlParams params )
    {
        if ( params.request == null || params.request.getRawPath().startsWith( "/api/" ) )
        {
            final ProjectName projectName = ProjectName.from( params.project );
            final Branch branch = Branch.from( params.branch );
            final String siteKey = params.siteKey;

            return new ConfigBaseUrlStrategy( projectName, branch, siteKey );
        }

        if ( params.request instanceof PortalRequest portalRequest )
        {
            return new RequestBaseUrlStrategy( portalRequest, params.urlType );
        }

        throw new IllegalArgumentException( "Missing project, branch or siteKey" );
    }
}
