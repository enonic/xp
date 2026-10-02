package com.enonic.xp.lib.portal.url;

import com.enonic.xp.portal.url.AttachmentUrlParts;
import com.enonic.xp.portal.url.ImageUrlParts;
import com.enonic.xp.portal.url.PageUrlParts;
import com.enonic.xp.portal.url.ProcessedHtml;
import com.enonic.xp.script.serializer.MapGenerator;
import com.enonic.xp.script.serializer.MapSerializable;
import com.enonic.xp.style.ImageStyle;

/**
 * Serializes a {@link ProcessedHtml} to a script object, under the names of the record components.
 */
final class ProcessedHtmlMapper
    implements MapSerializable
{
    private final ProcessedHtml processedHtml;

    ProcessedHtmlMapper( final ProcessedHtml processedHtml )
    {
        this.processedHtml = processedHtml;
    }

    @Override
    public void serialize( final MapGenerator gen )
    {
        gen.value( "html", processedHtml.html() );
        gen.value( "baseUrl", processedHtml.baseUrl() );

        gen.array( "links" );
        for ( final ProcessedHtml.Link link : processedHtml.links() )
        {
            gen.map();
            switch ( link )
            {
                case ProcessedHtml.ContentLink content ->
                {
                    gen.value( "type", "content" );
                    serializeLink( gen, link );
                    if ( content.page() != null )
                    {
                        serialize( gen, "page", content.page() );
                    }
                    gen.value( "fragment", content.fragment() );
                }
                case ProcessedHtml.AttachmentLink attachment ->
                {
                    gen.value( "type", "attachment" );
                    serializeLink( gen, link );
                    if ( attachment.attachment() != null )
                    {
                        serialize( gen, "attachment", attachment.attachment() );
                    }
                    gen.value( "download", attachment.download() );
                }
            }
            gen.end();
        }
        gen.end();

        gen.array( "images" );
        for ( final ProcessedHtml.Image image : processedHtml.images() )
        {
            gen.map();
            gen.value( "ref", image.ref() );
            gen.value( "contentId", image.contentId() );
            if ( image.style() != null )
            {
                serialize( gen, "style", image.style() );
            }
            if ( image.src() != null )
            {
                serialize( gen, "src", image.src() );
            }
            gen.array( "srcset" );
            for ( final ProcessedHtml.Source source : image.srcset() )
            {
                gen.map();
                gen.value( "width", source.width() );
                serialize( gen, "url", source.url() );
                gen.end();
            }
            gen.end();
            gen.end();
        }
        gen.end();
    }

    private static void serializeLink( final MapGenerator gen, final ProcessedHtml.Link link )
    {
        gen.value( "ref", link.ref() );
        gen.value( "uri", link.uri() );
        gen.value( "contentId", link.contentId() );
    }

    private static void serialize( final MapGenerator gen, final String key, final PageUrlParts parts )
    {
        gen.map( key );
        UrlPartsMapper.of( parts ).serialize( gen );
        gen.end();
    }

    private static void serialize( final MapGenerator gen, final String key, final AttachmentUrlParts parts )
    {
        gen.map( key );
        UrlPartsMapper.of( parts ).serialize( gen );
        gen.end();
    }

    private static void serialize( final MapGenerator gen, final String key, final ImageUrlParts parts )
    {
        gen.map( key );
        UrlPartsMapper.of( parts ).serialize( gen );
        gen.end();
    }

    private static void serialize( final MapGenerator gen, final String key, final ImageStyle style )
    {
        gen.map( key );
        gen.value( "name", style.getName() );
        gen.value( "aspectRatio", style.getAspectRatio() );
        gen.value( "filter", style.getFilter() );
        gen.end();
    }
}
