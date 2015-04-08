package com.enonic.xp.media;

import com.google.common.annotations.Beta;
import com.google.common.io.ByteSource;

@Beta
public interface MediaInfoService
{
    MediaInfo parseMediaInfo( ByteSource byteSource );

    Integer getOrientation( ByteSource byteSource );
}
