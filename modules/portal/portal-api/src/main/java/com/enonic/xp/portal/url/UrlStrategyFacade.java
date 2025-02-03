package com.enonic.xp.portal.url;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.project.ProjectName;

public interface UrlStrategyFacade
{
    PathPrefixStrategy requestPathPrefixStrategy( final PortalRequest portalRequest );

    PathPrefixStrategy contextPathPrefixStrategy(  final ProjectName projectName,
                                                  final Branch branch, final String contentKey );

    BaseUrlStrategy offlineBaseUrlStrategy( final ProjectName projectName, final Branch branch, final String siteKey );

    BaseUrlStrategy requestBaseUrlStrategy( final PortalRequest portalRequest, final String urlType );

    RewritePathStrategy requestRewriteStrategy( final PortalRequest portalRequest );

    RewritePathStrategy doNotRewriteStrategy();

    ImageUrlGeneratorParams offlineImageUrlParams( ImageUrlParams params );

    ImageUrlGeneratorParams requestImageUrlParams( ImageUrlParams params );
}
