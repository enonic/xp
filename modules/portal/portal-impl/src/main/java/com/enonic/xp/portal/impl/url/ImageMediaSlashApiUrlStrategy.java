package com.enonic.xp.portal.impl.url;

import java.util.Objects;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.portal.url.ImageMediaUrlParams;

import static com.enonic.xp.portal.impl.url.UrlBuilderHelper.appendPart;

public class ImageMediaSlashApiUrlStrategy
    implements MediaUrlStrategy
{
    private final ContentService contentService;

    public ImageMediaSlashApiUrlStrategy( final ContentService contentService )
    {
        this.contentService = contentService;
    }

    @Override
    public String generateUrl( final ImageMediaUrlParams params )
    {
        final String projectName = Objects.requireNonNull( params.getProjectName() );
        final Branch branch = Branch.from( Objects.requireNonNullElse( params.getBranch(), "master" ) );

        final StringBuilder url = new StringBuilder();

        appendPart( url, "api" );
        appendPart( url, "media" );
        appendPart( url, "image" );
        appendPart( url, branch == ContentConstants.BRANCH_DRAFT ? projectName + ":" + branch.getValue() : projectName );

        Content content = null;
        if ( params.getContentId() != null )
        {
            content = contentService.getById( ContentId.from( params.getContentId() ) );
        }
        if ( params.getContentPath() != null )
        {
            content = contentService.getByPath( ContentPath.from( params.getContentPath() ) );
        }

        final ImagePathResolver imagePathResolver = new ImagePathResolver( content );
        final String imagePath = imagePathResolver.resolve( params );

        appendPart( url, imagePath );

        return url.toString();
    }
}
