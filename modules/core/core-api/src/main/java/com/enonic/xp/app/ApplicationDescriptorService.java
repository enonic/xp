package com.enonic.xp.app;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public interface ApplicationDescriptorService
{
    @Nullable
    ApplicationDescriptor get( @NonNull ApplicationKey key );
}
