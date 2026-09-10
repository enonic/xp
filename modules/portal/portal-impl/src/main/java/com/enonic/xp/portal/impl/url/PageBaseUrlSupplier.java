package com.enonic.xp.portal.impl.url;

import java.util.function.Supplier;

import com.enonic.xp.content.ContentService;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalRequestAccessor;
import com.enonic.xp.portal.impl.PortalRequestHelper;
import com.enonic.xp.portal.url.PageUrlParams;
import com.enonic.xp.project.ProjectService;

final class PageBaseUrlSupplier
    implements Supplier<String>
{
    private final ContentService contentService;

    private final ProjectService projectService;

    private final PageUrlParams params;

    PageBaseUrlSupplier( ContentService contentService, ProjectService projectService, PageUrlParams params )
    {
        this.contentService = contentService;
        this.projectService = projectService;
        this.params = params;
    }

    @Override
    public String get()
    {
        final PortalRequest portalRequest = PortalRequestAccessor.get();

        // selecting a site is a statement about where the URL belongs, which is what following
        // the request would otherwise decide - so it takes the request out of play
        final boolean preferSiteRequest = params.getBaseUrl() == null && params.getBase() == null &&
            PortalRequestHelper.isSiteBase( portalRequest ) && params.getProjectName() == null && params.getBranch() == null;

        final String baseUrl =
            new ContentBaseUrlResolver( contentService, projectService, PageBase.params( params ), params.getBaseUrl(),
                                        preferSiteRequest ).resolve( metadata -> {
                if ( preferSiteRequest )
                {
                    return new ContentPathResolver().portalRequest( portalRequest )
                        .contentService( this.contentService )
                        .id( params.getId() )
                        .path( params.getPath() )
                        .resolve()
                        .toString();
                }

                return ContentPathResolver.relativeToAnchor( PageBase.contentPath( contentService, params, metadata ),
                                                             metadata.getAnchorPath() );
            } );

        return preferSiteRequest ? UrlBuilderHelper.rewriteUri( portalRequest.getRawRequest(), params.getType(), baseUrl ) : baseUrl;
    }
}
