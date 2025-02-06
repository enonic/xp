package com.enonic.xp.portal.url;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.Content;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.project.ProjectName;

public interface UrlStrategyFacade
{
    BaseUrlStrategy offlineBaseUrlStrategy( final ProjectName projectName, final Branch branch, final Content content );

    BaseUrlStrategy requestBaseUrlStrategy( final PortalRequest portalRequest, final String urlType );

    RewritePathStrategy requestRewriteStrategy( final PortalRequest portalRequest );

    RewritePathStrategy doNotRewriteStrategy();

    ImageUrlGeneratorParams offlineImageUrlParams( ImageUrlParams params );

    ImageUrlGeneratorParams requestImageUrlParams( ImageUrlParams params );
}
