package com.enonic.xp.resource;

import java.io.InputStream;
import java.io.Reader;
import java.util.List;

import com.google.common.io.ByteSource;

public interface Resource
{
    ResourceKey getKey();

    void requireExists();

    boolean exists();

    long getSize();

    /**
     * Returns the last modification time of this resource, in milliseconds since the epoch, or a negative
     * value when it cannot be determined.
     *
     * @return the last modification time in milliseconds since the epoch, or a negative value if unknown.
     * @deprecated Not a dependable measure of when a resource last changed. A resource served from an
     * application bundle reports the time recorded in its jar entry, and build tools normalize that value
     * to a constant so that builds are reproducible - Gradle does so by default as of version 9. Two
     * different builds of an application therefore produce identical timestamps, making the value a
     * property of the build rather than of the file. Only resources backed by a repository node carry a
     * genuine modification time; for file-backed resources the value is meaningful in development mode
     * alone. To detect that the resources of an application may have changed, compare
     * {@link com.enonic.xp.app.Application#getModifiedTime()} instead.
     */
    @Deprecated
    long getTimestamp();

    InputStream openStream();

    Reader openReader();

    String readString();

    byte[] readBytes();

    List<String> readLines();

    ByteSource getBytes();

    String getResolverName();
}
