package com.enonic.xp.app;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public interface ApplicationDescriptorService
{
    @Nullable
    ApplicationDescriptor get( @NonNull ApplicationKey key );

    /**
     * Creates or updates the persisted application descriptor ({@code enonic.yaml} below the application node in system-repo) and
     * its icon. The editor receives the current descriptor, or a descriptor holding only the key when the application has no
     * descriptor yet. An application without a node in system-repo gets one, so a descriptor can be created before any bundle
     * is installed. A bundle installed later persists its own descriptor over it.
     * <p>
     * The icon must be {@code image/svg+xml} or {@code image/png} and at most 100 KB.
     * Requires the {@code system.admin} or {@code system.schema.admin} role.
     *
     * @return the descriptor as it is read after the change
     */
    @NonNull
    ApplicationDescriptor createOrUpdate( @NonNull CreateOrUpdateApplicationDescriptorParams params );
}
