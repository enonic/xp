package com.enonic.xp.portal.impl.url;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

import com.google.common.collect.Multimap;
import com.google.common.hash.Hashing;
import com.google.common.io.Files;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentNotFoundException;
import com.enonic.xp.content.Media;
import com.enonic.xp.portal.url.ImageUrlParams;
import com.enonic.xp.web.HttpStatus;
import com.enonic.xp.web.WebException;

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

        appendPart( url, media.getId() + ":" + hash );
        appendPart( url, scale );
        appendPart( url, name );

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
        try
        {
            content = new ContentResolver().portalRequest( this.portalRequest )
                .contentService( this.contentService )
                .id( this.params.getId() )
                .path( this.params.getPath() )
                .resolve();
        }
        catch ( ContentNotFoundException e )
        {
            throw new WebException( HttpStatus.NOT_FOUND, String.format( "Image [%s] not found",
                                                                         Objects.requireNonNullElse( this.params.getId(),
                                                                                                     this.params.getPath() ) ), e );
        }
        if ( !content.getType().isDescendantOfMedia() && !content.getType().isMedia() )
        {
            throw WebException.notFound( String.format( "Image with [%s] id not found", content.getId() ) );

        }
        return (Media) content;
    }

    private String resolveHash( final Media media )
    {
        String binaryKey = this.contentService.getBinaryKey( media.getId(), media.getMediaAttachment().getBinaryReference() );
        return Hashing.sha1().
            newHasher().
            putString( String.valueOf( binaryKey ), StandardCharsets.UTF_8 ).
            putString( String.valueOf( media.getFocalPoint() ), StandardCharsets.UTF_8 ).
            putString( String.valueOf( media.getCropping() ), StandardCharsets.UTF_8 ).
            putString( String.valueOf( media.getOrientation() ), StandardCharsets.UTF_8 ).
            hash().
            toString();
    }

    private String resolveName( final Media media )
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
}
