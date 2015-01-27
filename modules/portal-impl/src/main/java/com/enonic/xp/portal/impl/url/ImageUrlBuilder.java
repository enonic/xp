package com.enonic.xp.portal.impl.url;

import com.google.common.collect.Multimap;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentId;
import com.enonic.xp.portal.url.ImageUrlParams;

final class ImageUrlBuilder
    extends PortalUrlBuilder<ImageUrlParams>
{
    @Override
    protected void buildUrl( final StringBuilder url, final Multimap<String, String> params )
    {
        super.buildUrl( url, params );
        appendPart( url, this.context.getContentPath().toString() );
        appendPart( url, "_" );
        appendPart( url, "image" );

        final ContentId id = resolveId();
        final String name = resolveName( id );

        appendPart( url, id.toString() );
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
            return name + "." + this.params.getFormat();
        }
        else
        {
            return name;
        }
    }

    private ContentId resolveId()
    {
        return new ContentIdResolver().
            context( this.context ).
            contentService( this.contentService ).
            id( this.params.getId() ).
            path( this.params.getPath() ).
            resolve();
    }
}
