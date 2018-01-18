package com.enonic.xp.portal.impl.url;

import org.apache.commons.lang.StringUtils;

import com.google.common.base.Charsets;
import com.google.common.collect.Multimap;
import com.google.common.hash.Hashing;
import com.google.common.io.Files;

import com.enonic.xp.attachment.Attachment;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.Media;
import com.enonic.xp.portal.url.ImageUrlParams;

final class ImageUrlBuilder
    extends GenericEndpointUrlBuilder<ImageUrlParams>
{
    public ImageUrlBuilder()
    {
        super( "image" );
    }

    @Override
    protected void buildUrl( final StringBuilder url, final Multimap<String, String> params )
    {
        super.buildUrl( url, params );

        final ContentId id = resolveId();
        final Media media = resolveMedia( id );
        final String hash = resolveHash( media );
        final String name = resolveName( media );
        final String scale = resolveScale();

        appendPart( url, id.toString() + ":" + hash );
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

    private Media resolveMedia( final ContentId id )
    {
        return (Media) this.contentService.getById( id );
    }

    private String resolveHash( final Media media )
    {
        final Attachment mediaAttachment = media.getMediaAttachment();
        String binaryKey = this.contentService.getBinaryKey( media.getId(), mediaAttachment.getBinaryReference() );
        String key = binaryKey + media.getFocalPoint() + media.getCropping() + media.getOrientation();
        return Hashing.sha1().
            newHasher().
            putString( key, Charsets.UTF_8 ).
            hash().
            toString();
    }

    private String resolveName( final Media media )
    {
        final String name = media.getName().toString();

        if ( this.params.getFormat() != null )
        {
            final String extension = Files.getFileExtension( name );
            if ( StringUtils.isEmpty( extension ) || !this.params.getFormat().equals( extension ) )
            {
                return name + "." + this.params.getFormat();
            }
        }
        return name;
    }

    private ContentId resolveId()
    {
        return new ContentIdResolver().
            portalRequest( this.portalRequest ).
            contentService( this.contentService ).
            id( this.params.getId() ).
            path( this.params.getPath() ).
            resolve();
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
