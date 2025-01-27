package com.enonic.xp.portal.impl.url;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.impl.ContentResolverResult;
import com.enonic.xp.portal.url.ImageMediaUrlParams;
import com.enonic.xp.repository.RepositoryUtils;

import static com.enonic.xp.portal.impl.url.UrlBuilderHelper.appendPart;

public class ImageMediaHarmonizedUrlStrategy
    implements MediaUrlStrategy
{
    private final ContentService contentService;

    public ImageMediaHarmonizedUrlStrategy( ContentService contentService )
    {
        this.contentService = contentService;
    }

    @Override
    public String generateUrl( final ImageMediaUrlParams params )
    {
        final StringBuilder url = new StringBuilder();

        final PortalRequest portalRequest = (PortalRequest) params.getWebRequest();

        final String projectName = RepositoryUtils.getContentRepoName( portalRequest.getRepositoryId() );
        final Branch branch = portalRequest.getBranch();

        final ContentResolverResult contentResolverResult =
            new com.enonic.xp.portal.impl.ContentResolver( contentService ).resolve( portalRequest );

        if ( contentResolverResult.getNearestSite() != null )
        {
            appendPart( url, portalRequest.getBaseUri() );
            appendPart( url, projectName );
            appendPart( url, branch.getValue() );
            appendPart( url, contentResolverResult.getNearestSite().getPath().toString() );
        }
        else
        {
            appendPart( url, "site" );
            appendPart( url, projectName );
            appendPart( url, branch.getValue() );
        }

        appendPart( url, "_" );

        appendPart( url, "media" );
        appendPart( url, "image" );
        appendPart( url, branch == ContentConstants.BRANCH_DRAFT ? projectName + ":" + branch.getValue() : projectName );

        final ContentResolver contentResolver = new ContentResolver().portalRequest( portalRequest )
            .contentService( this.contentService )
            .id( params.getContentId() )
            .path( params.getContentPath() );

        final Content content = contentResolver.resolve();

        final ImagePathResolver imagePathResolver = new ImagePathResolver( content );
        final String imagePath = imagePathResolver.resolve( params );

        appendPart( url, imagePath );

        return url.toString();
    }
}
