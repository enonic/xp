package com.enonic.xp.media;

import com.google.common.io.ByteSource;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.content.Media;

@PublicApi
public interface MediaInfoService
{
    MediaInfo parseMediaInfo( ByteSource byteSource );

    ImageOrientation getImageOrientation( ByteSource byteSource );

    ImageOrientation getImageOrientation( ByteSource byteSource, Media media );
}
