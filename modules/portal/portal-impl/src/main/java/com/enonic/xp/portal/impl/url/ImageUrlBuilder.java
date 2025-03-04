package com.enonic.xp.portal.impl.url;

import com.google.common.collect.Multimap;
import com.google.common.io.Files;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.Media;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.exception.NotFoundException;
import com.enonic.xp.portal.impl.MediaHashResolver;
import com.enonic.xp.portal.url.ImageUrlParams;

import static com.google.common.base.Strings.isNullOrEmpty;

final class ImageUrlBuilder
    extends GenericEndpointUrlBuilder<ImageUrlParams>
{
    ImageUrlBuilder()
    {
        super( "image" );
    }

    @Override
    protected void buildUrl( final StringBuilder url, final Multimap<String, String> params )
    {
        super.buildUrl( url, params );

        final Media media = resolveMedia();
        final String hash = resolveHash( media );
        final String name = resolveName( media );
        final String scale = resolveScale();

        UrlBuilderHelper.appendPart( url, media.getId() + ":" + hash );
        UrlBuilderHelper.appendPart( url, scale );
        UrlBuilderHelper.appendAndEncodePathParts( url, name );

        addParamIfNeeded( params, "quality", this.params.getQuality() );
        addParamIfNeeded( params, "background", this.params.getBackground() );
        addParamIfNeeded( params, "filter", this.params.getFilter() );
    }

    private void addParamIfNeeded( final Multimap<String, String> params, final String name, final Object value )
    {
        if ( value != null )
        {
            params.put( name, value.toString() );
        }
    }

    private Media resolveMedia()
    {
        final Content content;
        final ContentResolver contentResolver = new ContentResolver().portalRequest( this.portalRequest )
            .contentService( this.contentService )
            .id( this.params.getId() )
            .path( this.params.getPath() );
        content = contentResolver.resolve();
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
        String binaryKey = this.contentService.getBinaryKey( media.getId(), media.getMediaAttachment().getBinaryReference() );
        return MediaHashResolver.resolveLegacyImageHash( media, binaryKey );
    }

    private String resolveName( final Content media )
    {
        final String name = media.getName().toString();

        if ( this.params.getFormat() != null )
        {
            final String extension = Files.getFileExtension( name );
            if ( isNullOrEmpty( extension ) || !this.params.getFormat().equals( extension ) )
            {
                return name + "." + this.params.getFormat();
            }
        }
        return name;
    }

    private String resolveScale()
    {
        if ( this.params.getScale() == null )
        {
            throw new IllegalArgumentException( "Missing mandatory parameter 'scale' for image URL" );
        }

        return this.params.getScale().replaceAll( "\\s", "" ).replaceAll( "[(,]", "-" ).replace( ")", "" );
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
