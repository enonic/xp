package com.enonic.xp.tools.gradle.watch;

import java.io.Closeable;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.WatchKey;

interface Watcher
    extends Closeable
{
    void register( Path path )
        throws IOException;

    void unregister( Path path )
        throws IOException;

    boolean isWatching( Path path );

    WatchKey take()
        throws InterruptedException;

    boolean isOpen();
}
