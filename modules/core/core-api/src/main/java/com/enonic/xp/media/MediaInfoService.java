package com.enonic.xp.media;

import com.google.common.io.ByteSource;


public interface MediaInfoService
{
    MediaInfo parseMediaInfo( ByteSource byteSource );

    @Deprecated
    ImageOrientation getImageOrientation( ByteSource byteSource );
}
