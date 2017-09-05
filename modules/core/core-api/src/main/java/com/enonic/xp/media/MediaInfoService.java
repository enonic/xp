package com.enonic.xp.media;

import com.google.common.annotations.Beta;
import com.google.common.io.ByteSource;

import com.enonic.xp.content.Content;

@Beta
public interface MediaInfoService
{
    MediaInfo parseMediaInfo( ByteSource byteSource );

    ImageOrientation getImageOrientation( ByteSource byteSource );

    ImageOrientation getImageOrientation( ByteSource byteSource, Content content );
}
