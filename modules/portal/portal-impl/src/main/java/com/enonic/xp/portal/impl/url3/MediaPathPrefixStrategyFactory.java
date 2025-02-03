package com.enonic.xp.portal.impl.url3;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.url.ImageMediaUrlParams;
import com.enonic.xp.portal.url.PathPrefixStrategy;
import com.enonic.xp.project.ProjectName;
import com.enonic.xp.web.WebRequest;

@Component(immediate = true, service = MediaPathPrefixStrategyFactory.class)
public class MediaPathPrefixStrategyFactory
{
    private final ContentService contentService;

    @Activate
    public MediaPathPrefixStrategyFactory( @Reference final ContentService contentService )
    {
        this.contentService = contentService;
    }

    public PathPrefixStrategy create( final ImageMediaUrlParams params )
    {
        final WebRequest webRequest = params.getWebRequest();

        if ( webRequest == null )
        {
            return new SlashApiPathPrefixStrategy();
        }

        if ( webRequest instanceof PortalRequest portalRequest )
        {
            return new HarmonizedApiPathPrefixStrategy(
                HarmonizedApiPathPrefixStrategyParams.create().setPortalRequest( portalRequest ).build() );
        }

        final String projectName = params.getProjectName();
        final String branch = params.getBranch();
        final String siteKey = params.getSiteKey();

        if ( projectName == null || branch == null || siteKey == null )
        {
            throw new IllegalArgumentException( "Missing project, branch or siteKey" );
        }

        return new HarmonizedApiPathPrefixStrategy( HarmonizedApiPathPrefixStrategyParams.create()
                                                        .setProjectName( ProjectName.from( projectName ) )
                                                        .setBranch( Branch.from( branch ) )
                                                        .setContentKey( siteKey )
                                                        .build() );
    }
}
