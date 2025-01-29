package com.enonic.xp.portal.impl.url3;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.impl.url2.ImageMediaUrlParams;
import com.enonic.xp.project.ProjectName;
import com.enonic.xp.web.WebRequest;

public class MediaPathPrefixStrategyFactory
{
    public static PathPrefixStrategy create( final ImageMediaUrlParams params )
    {
        final WebRequest request = params.request;

        if ( request == null || request.getRawPath().startsWith( "/api/" ) )
        {
            return new SlashApiPathPrefixStrategy();
        }

        if ( request instanceof PortalRequest portalRequest )
        {
            return new HarmonizedApiPathPrefixStrategy( portalRequest );
        }

        if ( params.project == null || params.branch == null || params.siteKey == null )
        {
            throw new IllegalArgumentException( "Missing project, branch or siteKey" );
        }

        return new ContextPathPrefixStrategy( ProjectName.from( params.project ), Branch.from( params.branch ), params.siteKey );
    }
}
