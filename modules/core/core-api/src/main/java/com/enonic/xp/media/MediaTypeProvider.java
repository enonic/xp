package com.enonic.xp.media;

import java.util.Map;

import com.google.common.net.MediaType;

public interface MediaTypeProvider
{
    MediaType fromExt( String ext );

    Map<String, MediaType> asMap();
}
