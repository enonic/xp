package com.enonic.xp.util;

import java.util.HashMap;

import com.google.common.annotations.Beta;
import com.google.common.io.Files;
import com.google.common.net.MediaType;

@Beta
public final class MediaTypes
    extends HashMap<String, MediaType>
{
    private final static MediaTypes INSTANCE = new MediaTypes();

    private final static MediaType DEFAULT = MediaType.OCTET_STREAM;

    private MediaTypes()
    {
        put( "gif", MediaType.GIF );
        put( "png", MediaType.PNG );
        put( "jpeg", MediaType.JPEG );
        put( "jpg", MediaType.JPEG );
        put( "pdf", MediaType.PDF );
        put( "json", MediaType.JSON_UTF_8.withoutParameters() );
        put( "js", MediaType.JAVASCRIPT_UTF_8.withoutParameters() );
        put( "css", MediaType.CSS_UTF_8.withoutParameters() );
        put( "html", MediaType.HTML_UTF_8.withoutParameters() );
        put( "xml", MediaType.XML_UTF_8.withoutParameters() );
        put( "svg", MediaType.SVG_UTF_8.withoutParameters() );
        put( "txt", MediaType.PLAIN_TEXT_UTF_8.withoutParameters() );
    }

    public MediaType fromExt( final String ext )
    {
        final MediaType type = get( ext );
        return type != null ? type : DEFAULT;
    }

    public MediaType fromFile( final String fileName )
    {
        return fromExt( Files.getFileExtension( fileName ) );
    }

    @Override
    public MediaType put( final String ext, final MediaType value )
    {
        return super.put( ext, value.withoutParameters() );
    }

    public static MediaTypes instance()
    {
        return INSTANCE;
    }
}
