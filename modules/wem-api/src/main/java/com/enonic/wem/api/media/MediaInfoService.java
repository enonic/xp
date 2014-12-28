package com.enonic.wem.api.media;

import com.google.common.io.ByteSource;

public interface MediaInfoService
{
    public MediaInfo parseMediaInfo( ByteSource byteSource );
}
