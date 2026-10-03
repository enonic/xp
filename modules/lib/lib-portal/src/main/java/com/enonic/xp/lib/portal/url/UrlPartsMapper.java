package com.enonic.xp.lib.portal.url;

import com.enonic.xp.portal.url.AttachmentUrlParts;
import com.enonic.xp.portal.url.ImageUrlParts;
import com.enonic.xp.portal.url.PageUrlParts;
import com.enonic.xp.script.serializer.MapGenerator;
import com.enonic.xp.script.serializer.MapSerializable;

/**
 * Serializes the parts of a page, image or attachment URL to a script object, under the names of the record components.
 */
final class UrlPartsMapper
    implements MapSerializable
{
    private final Object parts;

    private UrlPartsMapper( final Object parts )
    {
        this.parts = parts;
    }

    static UrlPartsMapper of( final PageUrlParts parts )
    {
        return new UrlPartsMapper( parts );
    }

    static UrlPartsMapper of( final ImageUrlParts parts )
    {
        return new UrlPartsMapper( parts );
    }

    static UrlPartsMapper of( final AttachmentUrlParts parts )
    {
        return new UrlPartsMapper( parts );
    }

    @Override
    public void serialize( final MapGenerator gen )
    {
        switch ( parts )
        {
            case PageUrlParts page ->
            {
                gen.value( "baseUrl", page.baseUrl() );
                gen.value( "path", page.path() );
                gen.value( "queryString", page.queryString() );
            }
            case ImageUrlParts image ->
            {
                gen.value( "path", image.path() );
                gen.value( "queryString", image.queryString() );
                gen.value( "context", image.context() );
                gen.value( "id", image.id() );
                gen.value( "fingerprint", image.fingerprint() );
                gen.value( "scale", image.scale() );
                gen.value( "name", image.name() );
            }
            case AttachmentUrlParts attachment ->
            {
                gen.value( "path", attachment.path() );
                gen.value( "queryString", attachment.queryString() );
                gen.value( "context", attachment.context() );
                gen.value( "id", attachment.id() );
                gen.value( "fingerprint", attachment.fingerprint() );
                gen.value( "name", attachment.name() );
            }
            default -> throw new IllegalStateException( "Unknown URL parts: " + parts.getClass() );
        }
    }
}
