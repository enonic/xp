package com.enonic.xp.core.impl.app.resource;

import org.jspecify.annotations.Nullable;

/**
 * Identity of a processed value: the segment and key of the processor, and the source the context of the read asks the resources
 * from ({@code null} for the source of the application as it is served). A read asking for the bundle alone is a value of its
 * own, neither served from nor evicting what was read from the persisted schema.
 */
record ProcessingKey(String segment, Object key, @Nullable String source)
{
}
