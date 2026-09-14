package com.enonic.xp.core.internal.image;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

import org.jspecify.annotations.NullMarked;

/** Provides access to the bundled ImageMagick distribution for the current platform. */
@NullMarked
public interface ImageMagick
{
    /**
     * Acquires an installation that remains available until the returned handle is closed.
     * Callers must close the handle after all processes using it have terminated.
     *
     * @return an installation handle
     * @throws IOException if the platform is unsupported, the installation cannot be prepared,
     *     or the service has stopped
     */
    Installation acquire() throws IOException;

    /** An installation retained for the lifetime of this handle. */
    interface Installation extends AutoCloseable
    {
        /**
         * Returns the executable to launch while this handle is open.
         *
         * @return the absolute executable path
         */
        Path executable();

        /**
         * Returns environment variables required by this installation.
         *
         * @return immutable environment settings
         */
        Map<String, String> environment();

        /**
         * Releases this handle. Repeated calls have no effect.
         *
         * @throws IOException if installation cleanup fails
         */
        @Override
        void close() throws IOException;
    }
}
