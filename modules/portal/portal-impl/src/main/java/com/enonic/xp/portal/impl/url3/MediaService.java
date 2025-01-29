package com.enonic.xp.portal.impl.url3;

import java.util.Objects;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.content.Media;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.exception.NotFoundException;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.impl.url2.ImageMediaUrlParams;
import com.enonic.xp.project.ProjectName;

public class MediaService
{
    private final ContentService contentService;

    public MediaService( final ContentService contentService )
    {
        this.contentService = contentService;
    }

    public String imageMediaUrl( final ImageMediaUrlParams params )
    {
        final PathPrefixStrategy pathPrefixStrategy = MediaPathPrefixStrategyFactory.create( params );
        final BaseUrlStrategy baseUrlStrategy = BaseUrlStrategyFactory.create( params );

        final ProjectName projectName = getProjectName( params );
        final Branch branch = getBranch( params );

        final Context context =
            ContextBuilder.copyOf( ContextAccessor.current() ).repositoryId( projectName.getRepoId() ).branch( branch ).build();

        final Media media = context.callWith( () -> getMedia( Objects.requireNonNullElse( params.id, params.path ) ) );

        final ImageMediaPathStrategyParams imageMediaPathStrategyParams = ImageMediaPathStrategyParams.create()
            .setMedia( media )
            .setProjectName( projectName )
            .setBranch( branch )
            .setScale( params.scale )
            .build();

        final MediaPathStrategy mediaPathStrategy =
            new MediaPathStrategy( pathPrefixStrategy, new ImageMediaPathStrategy( imageMediaPathStrategyParams ) );

        return UrlGenerator.INSTANCE.generateUrl( baseUrlStrategy, mediaPathStrategy );
    }

    public String attachmentMediaUrl( final ImageMediaUrlParams params )
    {
        final PathPrefixStrategy pathPrefixStrategy = new HarmonizedApiPathPrefixStrategy( null );

        final MediaPathStrategy mediaPathStrategy =
            new MediaPathStrategy( pathPrefixStrategy, new AttachmentMediaPathStrategy( params.id ) );

        final BaseUrlStrategy baseUrlStrategy = new RequestBaseUrlStrategy( null, null );

        final UrlGenerator urlGenerator = new UrlGenerator();
        return urlGenerator.generateUrl( baseUrlStrategy, mediaPathStrategy );
    }

    private ProjectName getProjectName( final ImageMediaUrlParams params )
    {
        if ( params.request instanceof PortalRequest portalRequest )
        {
            if ( !portalRequest.isSiteBase() )
            {
                throw new IllegalArgumentException( "PortalRequest must be site based" );
            }
            return ProjectName.from( portalRequest.getRepositoryId() );
        }
        else
        {
            return ProjectName.from( Objects.requireNonNull( params.project ) );
        }
    }

    private Branch getBranch( final ImageMediaUrlParams params )
    {
        if ( params.request instanceof PortalRequest portalRequest )
        {
            if ( !portalRequest.isSiteBase() )
            {
                throw new IllegalArgumentException( "PortalRequest must be site based" );
            }
            return portalRequest.getBranch();
        }
        else
        {
            return Branch.from( Objects.requireNonNullElse( params.branch, "master" ) );
        }
    }

    private Media getMedia( final String contentKey )
    {
        Content content;
        if ( contentKey.startsWith( "/" ) )
        {
            content = contentService.getByPath( ContentPath.from( contentKey ) );
        }
        else
        {
            content = contentService.getById( ContentId.from( contentKey ) );
        }

        if ( !( content instanceof Media ) )
        {
            throw new ContentInNotMediaException( String.format( "Content [%s] is not a Media", contentKey ) );
        }

        return (Media) content;
    }

    private static class ContentInNotMediaException
        extends NotFoundException
    {
        ContentInNotMediaException( final String message )
        {
            super( message );
        }
    }

}
