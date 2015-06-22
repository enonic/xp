package com.enonic.xp.portal.impl.url;

import org.apache.commons.lang.StringUtils;

import com.google.common.collect.Multimap;
import com.google.common.io.Files;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.portal.url.ImageUrlParams;

final class ImageUrlBuilder
    extends PortalUrlBuilder<ImageUrlParams>
{
    @Override
    protected void buildUrl( final StringBuilder url, final Multimap<String, String> params )
    {
        super.buildUrl( url, params );
        appendPart( url, this.portalRequest.getContentPath().toString() );
        appendPart( url, "_" );
        appendPart( url, "image" );

        final ContentId id = resolveId();
        final String name = resolveName( id );
        final String scale = resolveScale();

        appendPart( url, id.toString() );
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

    private String resolveName( final ContentId id )
    {
        final Content content = this.contentService.getById( id );
        final String name = content.getName().toString();

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
        return this.params.getScale().replaceAll( "[(,]", "-" ).replace( ")", "" );
    }
}
