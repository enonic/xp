package com.enonic.xp.portal.impl.url2;

import java.util.Objects;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.content.Media;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.exception.NotFoundException;

public class MediaService
{

    private final ContentService contentService;

    private static final UrlGenerator urlGenerator = new UrlGenerator();

    public MediaService( final ContentService contentService )
    {
        this.contentService = contentService;
    }

    public String mediaImageUrl( final ImageMediaUrlParams params )
    {
        final Context context = ContextBuilder.copyOf( ContextAccessor.current() )
            .attribute( "pathGenerator.project", "project" )
            .attribute( "pathGenerator.branch", "master" )
            .attribute( "pathGenerator.contentKey", "id" )
            .build();

        final Media media = getMedia( Objects.requireNonNullElse( params.id, params.path ) );

        final ImageMediaPathGenerator pathGenerator = new ImageMediaPathGenerator( null, params, () -> "/api", id -> media );
        final ImageMediaPathRewriter pathRewriter = new ImageMediaPathRewriter();
        final ImageMediaBaseUrlResolver baseUrlResolver = new ImageMediaBaseUrlResolver();

        final UrlGeneratorParams urlParams = new UrlGeneratorParams( pathGenerator, pathRewriter, baseUrlResolver );

        return context.callWith( () -> urlGenerator.generateUrl( urlParams ) );
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
