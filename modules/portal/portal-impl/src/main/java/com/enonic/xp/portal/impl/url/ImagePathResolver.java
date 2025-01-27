package com.enonic.xp.portal.impl.url;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.io.Files;

import com.enonic.xp.attachment.Attachment;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.Media;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.exception.NotFoundException;
import com.enonic.xp.portal.url.ImageMediaUrlParams;

import static com.enonic.xp.portal.impl.url.UrlBuilderHelper.appendParams;
import static com.enonic.xp.portal.impl.url.UrlBuilderHelper.appendPart;
import static com.google.common.base.Strings.isNullOrEmpty;

public final class ImagePathResolver
{
    private final Content content;

    public ImagePathResolver( final Content content )
    {
        this.content = content;
    }

    public String resolve( final ImageMediaUrlParams params )
    {
        final StringBuilder url = new StringBuilder();

        final Media media = resolveMedia( content );
        final String hash = resolveHash( media );
        final String name = resolveName( media, params.getFormat() );
        final String scale = resolveScale( params.getScale() );

        appendPart( url, media.getId() + ( hash != null ? ":" + hash : "" ) );
        appendPart( url, scale );
        appendPart( url, name );

        final Multimap<String, String> queryParams = LinkedListMultimap.create();
        queryParams.putAll( params.getQueryParams() );
        if ( params.getQuality() != null )
        {
            queryParams.put( "quality", params.getQuality().toString() );
        }
        if ( params.getBackground() != null )
        {
            queryParams.put( "background", params.getBackground() );
        }
        if ( params.getFilter() != null )
        {
            queryParams.put( "filter", params.getFilter() );
        }
        appendParams( url, queryParams.entries() );

        return url.toString();
    }

    private Media resolveMedia( final Content content )
    {
        if ( !( content instanceof Media ) )
        {
            throw new ContentInNotMediaException(
                String.format( "Content [%s:%s:%s] is not a Media", ContextAccessor.current().getRepositoryId(),
                               ContextAccessor.current().getBranch(), content.getId() ) );
        }
        return (Media) content;
    }

    private String resolveHash( final Media media )
    {
        final Attachment attachment = media.getMediaAttachment();
        return attachment.getSha512() != null ? attachment.getSha512().substring( 0, 32 ) : null;
    }

    private String resolveName( final Content media, final String format )
    {
        final String name = media.getName().toString();

        if ( format != null )
        {
            final String extension = Files.getFileExtension( name );
            if ( isNullOrEmpty( extension ) || !format.equals( extension ) )
            {
                return name + "." + format;
            }
        }
        return name;
    }

    private String resolveScale( final String scale )
    {
        if ( scale == null )
        {
            throw new IllegalArgumentException( "Missing mandatory parameter 'scale' for image URL" );
        }

        return scale.replaceAll( "\\s", "" ).replaceAll( "[(,]", "-" ).replace( ")", "" );
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
