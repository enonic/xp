package com.enonic.xp.extractor;

import com.google.common.io.ByteSource;

public interface BinaryExtractor
{
    ExtractedData extract( ByteSource source );
}
