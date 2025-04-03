package com.enonic.xp.portal.impl.url;

import java.util.function.Supplier;

import com.enonic.xp.content.ContentService;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalRequestAccessor;
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
        final String baseUrl = new ContentBaseUrlResolver( contentService, projectService, params ).resolve( metadata -> null );

        final PortalRequest portalRequest = PortalRequestAccessor.get();
        if ( portalRequest != null && portalRequest.isSiteBase() && params.getProjectName() == null && params.getBranch() == null )
        {
            return UrlBuilderHelper.rewriteUri( portalRequest.getRawRequest(), params.getUrlType(), baseUrl );
        }
        return baseUrl;
    }
}
