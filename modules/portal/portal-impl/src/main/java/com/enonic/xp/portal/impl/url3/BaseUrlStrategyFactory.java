package com.enonic.xp.portal.impl.url3;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.url.BaseUrlStrategy;
import com.enonic.xp.portal.url.ImageMediaUrlParams;
import com.enonic.xp.project.ProjectName;
import com.enonic.xp.web.WebRequest;

@Component(immediate = true, service = BaseUrlStrategyFactory.class)
public class BaseUrlStrategyFactory
{
    private final ContentService contentService;

    @Activate
    public BaseUrlStrategyFactory( @Reference final ContentService contentService )
    {
        this.contentService = contentService;
    }

    public BaseUrlStrategy create( final ImageMediaUrlParams params )
    {
        final WebRequest webRequest = params.getWebRequest();

        if ( webRequest == null || webRequest.getRawPath().startsWith( "/api/" ) )
        {
            final ProjectName projectName = ProjectName.from( params.getProjectName() );
            final Branch branch = Branch.from( params.getBranch() );
            final String siteKey = params.getSiteKey();

            return new OfflineBaseUrlStrategy( contentService, projectName, branch, siteKey );
        }
        else if ( webRequest instanceof PortalRequest portalRequest )
        {
            return new RequestBaseUrlStrategy( portalRequest, params.getUrlType() );
        }
        else
        {
            throw new IllegalArgumentException( "Missing project, branch or siteKey" );
        }
    }
}
