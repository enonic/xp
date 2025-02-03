package com.enonic.xp.portal.impl.url3;

import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.url.PathPrefixStrategy;
import com.enonic.xp.project.ProjectName;
import com.enonic.xp.site.Site;

import static com.enonic.xp.portal.impl.url.UrlBuilderHelper.appendPart;

public class HarmonizedApiPathPrefixStrategy
    implements PathPrefixStrategy
{
    private final HarmonizedApiPathPrefixStrategyParams params;

    public HarmonizedApiPathPrefixStrategy( final HarmonizedApiPathPrefixStrategyParams params )
    {
        this.params = params;
    }

    @Override
    public String generatePathPrefix()
    {
        final StringBuilder path = new StringBuilder();

        final PortalRequest portalRequest = this.params.getPortalRequest();

        if ( portalRequest != null )
        {
            appendPart( path, portalRequest.getBaseUri() );
            appendPart( path, ProjectName.from( portalRequest.getRepositoryId() ).toString() );
            appendPart( path, portalRequest.getBranch().getValue() );
        }
        else
        {
            appendPart( path, "site" );
            appendPart( path, params.getProjectName().toString() );
            appendPart( path, params.getBranch().getValue() );

        }

//        final Site site = params.getNearestSiteStrategy().getNearestSite();
//
//        if ( site != null )
//        {
//            appendPart( path, site.getPath().toString() );
//        }
        appendPart( path, "_" );

        return path.toString();
    }
}
