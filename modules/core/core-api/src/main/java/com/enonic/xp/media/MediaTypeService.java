package com.enonic.xp.media;

import java.util.Map;

import com.google.common.net.MediaType;

public interface MediaTypeService
    extends Iterable<MediaTypeProvider>
{
    MediaType fromExt( String ext );

    MediaType fromFile( String fileName );

    Map<String, MediaType> asMap();
}
