package com.enonic.xp.media;

import com.google.common.io.ByteSource;

public interface MediaInfoService
{
    public MediaInfo parseMediaInfo( ByteSource byteSource );
}
