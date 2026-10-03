package com.enonic.xp.portal.impl.url;

import java.util.function.Supplier;

import com.enonic.xp.content.ContentService;
import com.enonic.xp.exception.NotFoundException;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalRequestAccessor;
import com.enonic.xp.portal.impl.PortalRequestHelper;
import com.enonic.xp.portal.url.ContentOutOfScopeException;
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

    /**
     * @throws PageNotFoundException if the page URL cannot be generated, so that it is answered with 404
     */
    @Override
    public String get()
    {
        try
        {
            return resolve();
        }
        catch ( ContentOutOfScopeException | NotFoundException e )
        {
            throw e;
        }
        catch ( RuntimeException e )
        {
            throw new PageNotFoundException( e );
        }
    }

    private String resolve()
    {
        final PortalRequest portalRequest = PortalRequestAccessor.get();

        final boolean preferSiteRequest = PortalRequestHelper.isSiteBase( portalRequest ) &&
            params.getProjectName() == null && params.getBranch() == null;

        final String baseUrl =
            new ContentBaseUrlResolver( contentService, projectService, PageBase.params( params ), preferSiteRequest ).resolve(
                metadata -> {
                    if ( preferSiteRequest )
                    {
                        return new ContentPathResolver().portalRequest( portalRequest )
                            .contentService( this.contentService )
                            .id( params.getId() )
                            .path( params.getPath() )
                            .resolve()
                            .toString();
                    }

                    return ContentPathResolver.relativeToAnchor( metadata.content().getPath(), PageBase.level( metadata ) );
                } );

        return preferSiteRequest ? UrlBuilderHelper.rewriteUri( portalRequest.getRawRequest(), params.getType(), baseUrl ) : baseUrl;
    }
}
