package com.enonic.xp.core.media;

import com.google.common.io.ByteSource;

public interface MediaInfoService
{
    public MediaInfo parseMediaInfo( ByteSource byteSource );
}
