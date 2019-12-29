package com.enonic.xp.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import com.google.common.io.Files;
import com.google.common.net.MediaType;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.media.MediaTypeProvider;
import com.enonic.xp.media.MediaTypeService;

@PublicApi
public final class MediaTypes
    implements MediaTypeService
{
    private final static MediaTypes INSTANCE = new MediaTypes();

    private final static MediaType DEFAULT = MediaType.OCTET_STREAM;

    private final List<MediaTypeProvider> providers = new CopyOnWriteArrayList<>();

    private final Map<String, MediaType> mediaTypes;

    private MediaTypes()
    {
        this.mediaTypes = new HashMap<>();

        this.mediaTypes.put( "gif", MediaType.GIF );
        this.mediaTypes.put( "png", MediaType.PNG );
        this.mediaTypes.put( "jpeg", MediaType.JPEG );
        this.mediaTypes.put( "jpg", MediaType.JPEG );
        this.mediaTypes.put( "pdf", MediaType.PDF );
        this.mediaTypes.put( "json", MediaType.JSON_UTF_8.withoutParameters() );
        this.mediaTypes.put( "js", MediaType.JAVASCRIPT_UTF_8.withoutParameters() );
        this.mediaTypes.put( "es", MediaType.JAVASCRIPT_UTF_8.withoutParameters() );
        this.mediaTypes.put( "es6", MediaType.JAVASCRIPT_UTF_8.withoutParameters() );
        this.mediaTypes.put( "mjs", MediaType.JAVASCRIPT_UTF_8.withoutParameters() );
        this.mediaTypes.put( "css", MediaType.CSS_UTF_8.withoutParameters() );
        this.mediaTypes.put( "html", MediaType.HTML_UTF_8.withoutParameters() );
        this.mediaTypes.put( "xml", MediaType.XML_UTF_8.withoutParameters() );
        this.mediaTypes.put( "svg", MediaType.SVG_UTF_8.withoutParameters() );
        this.mediaTypes.put( "txt", MediaType.PLAIN_TEXT_UTF_8.withoutParameters() );
        this.mediaTypes.put( "mp4", MediaType.MP4_VIDEO );
    }

    @Override
    public MediaType fromExt( final String ext )
    {
        for ( final MediaTypeProvider provider : this.providers )
        {
            final MediaType type = provider.fromExt( ext );
            if ( type != null )
            {
                return type;
            }
        }

        final MediaType type = this.mediaTypes.get( ext );
        return type != null ? type : DEFAULT;
    }

    @Override
    public MediaType fromFile( final String fileName )
    {
        return fromExt( Files.getFileExtension( fileName ) );
    }

    @Override
    public Map<String, MediaType> asMap()
    {
        final Map<String, MediaType> map = new HashMap<>( this.mediaTypes );
        for ( final MediaTypeProvider provider : this.providers )
        {
            map.putAll( provider.asMap() );
        }

        return map;
    }

    public void addProvider( final MediaTypeProvider provider )
    {
        this.providers.add( provider );
    }

    public void removeProvider( final MediaTypeProvider provider )
    {
        this.providers.remove( provider );
    }

    @Override
    public Iterator<MediaTypeProvider> iterator()
    {
        return this.providers.iterator();
    }

    public static MediaTypes instance()
    {
        return INSTANCE;
    }
}
