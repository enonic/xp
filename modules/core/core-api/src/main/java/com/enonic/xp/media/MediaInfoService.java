package com.enonic.xp.media;

import com.google.common.io.ByteSource;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public interface MediaInfoService
{
    MediaInfo parseMediaInfo( ByteSource byteSource );

    ImageOrientation getImageOrientation( ByteSource byteSource );
}
