package com.enonic.xp.core.impl.export.writer;

import java.io.Closeable;
import java.nio.file.Path;

import com.google.common.io.ByteSource;

public interface ExportWriter
    extends Closeable
{
    void writeElement( Path path, String export );

    void writeSource( Path itemPath, ByteSource source );
}
