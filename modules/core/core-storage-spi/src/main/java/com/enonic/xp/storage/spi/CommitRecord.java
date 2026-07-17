package com.enonic.xp.storage.spi;

import java.time.Instant;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import static java.util.Objects.requireNonNull;

/**
 * COMMIT document equivalent. Field set mirrors {@code CommitIndexPath} 1:1 so the
 * mapping from {@code CommitStorageRequestFactory} is mechanical.
 */
@NullMarked
public record CommitRecord(String commitId, @Nullable String message, @Nullable String committer, Instant timestamp)
{
    public CommitRecord
    {
        requireNonNull( commitId );
        requireNonNull( timestamp );
    }
}
