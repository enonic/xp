package com.enonic.xp.portal.impl.url;

import java.util.function.Supplier;

import com.enonic.xp.content.ContentService;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalRequestAccessor;
import com.enonic.xp.portal.impl.PortalRequestHelper;
import com.enonic.xp.portal.url.BaseUrlParams;
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
        final BaseUrlParams baseUrlParams = BaseUrlParams.create()
            .setUrlType( params.getType() )
            .setProjectName( params.getProjectName() )
            .setBranch( params.getBranch() )
            .setId( params.getId() )
            .setPath( params.getPath() )
            .setAnchor( params.getAnchor() )
            .build();

        final PortalRequest portalRequest = PortalRequestAccessor.get();

        final boolean preferSiteRequest = params.getBaseUrl() == null && params.getAnchor() == null &&
            PortalRequestHelper.isSiteBase( portalRequest ) && params.getProjectName() == null && params.getBranch() == null;

        final String baseUrl =
            new ContentBaseUrlResolver( contentService, projectService, baseUrlParams, params.getBaseUrl() ).resolve( metadata -> {
            if ( preferSiteRequest )
            {
                return new ContentPathResolver().portalRequest( portalRequest )
                    .contentService( this.contentService )
                    .id( params.getId() )
                    .path( params.getPath() )
                    .resolve()
                    .toString();
            }
            else
            {
                return ContentPathResolver.relativeToAnchor( metadata.getContent().getPath(), metadata.getAnchorPath() );
            }
        } );

        return preferSiteRequest ? UrlBuilderHelper.rewriteUri( portalRequest.getRawRequest(), params.getType(), baseUrl ) : baseUrl;
    }
}
