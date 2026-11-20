package com.enonic.xp.core.impl.app;

import org.jspecify.annotations.Nullable;
import org.osgi.framework.Bundle;

import com.enonic.xp.core.impl.app.resolver.ApplicationUrlResolver;

/**
 * Where the resources of an application are served from: its active bundle, if any, and the resolver serving its resources, if
 * any, both of one lookup. A value derived from the resources is stamped with the incarnation of the bundle, so the two must be of
 * one state of the application. Looked up one after the other, a bundle stopping in-between would leave a value computed by the
 * resolver of the running application stamped as served by no bundle, which is also what every later lookup finds.
 */
public record ResourceSource(@Nullable Bundle bundle, @Nullable ApplicationUrlResolver resolver)
{
    public static final ResourceSource NONE = new ResourceSource( null, null );

    /**
     * Whether values derived from the resources may be cached, see {@link ApplicationUrlResolver#isStable()}: nothing served is
     * nothing to cache.
     */
    public boolean isStable()
    {
        return resolver != null && resolver.isStable();
    }
}
