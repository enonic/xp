package com.enonic.xp.portal.impl.url;

import java.util.function.Supplier;

import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalRequestAccessor;
import com.enonic.xp.portal.impl.PortalRequestHelper;
import com.enonic.xp.portal.url.BaseUrlParams;
import com.enonic.xp.project.ProjectService;

final class ContentBaseUrlSupplier
    implements Supplier<String>
{
    private final ContentService contentService;

    private final ProjectService projectService;

    private final BaseUrlParams params;

    ContentBaseUrlSupplier( ContentService contentService, ProjectService projectService, BaseUrlParams params )
    {
        this.contentService = contentService;
        this.projectService = projectService;
        this.params = params;
    }

    @Override
    public String get()
    {
        final PortalRequest portalRequest = PortalRequestAccessor.get();

        final boolean followsRequest =
            PortalRequestHelper.isSiteBase( portalRequest ) && params.getProjectName() == null && params.getBranch() == null;

        // when the request is followed the base is the address of the level the virtual host
        // mounts, so that it is inside the mapping and can be rewritten into the host's own terms
        final String baseUrl = new ContentBaseUrlResolver( contentService, projectService, params, true ).resolve( metadata -> {
            if ( !followsRequest )
            {
                return null;
            }
            final ContentPath mounted = VhostLevel.resolve( metadata.getProjectName(), metadata.getBranch() );
            return mounted != null && !mounted.isRoot() ? mounted.toString() : null;
        } );

        if ( followsRequest )
        {
            return UrlBuilderHelper.rewriteUri( portalRequest.getRawRequest(), params.getUrlType(), baseUrl );
        }
        return baseUrl;
    }
}
