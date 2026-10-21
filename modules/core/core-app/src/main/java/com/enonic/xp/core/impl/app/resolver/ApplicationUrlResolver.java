package com.enonic.xp.core.impl.app.resolver;

import java.util.Set;

import com.enonic.xp.resource.Resource;

public interface ApplicationUrlResolver
{
    Set<String> findFiles();

    Resource findResource( String path );

    /**
     * Whether the resources served stay the same for as long as the generation of the application and its bundle do. A stable
     * resolver lets values derived from its resources be cached without re-reading them; an unstable one, serving files that
     * change under it without any signal (the classloader of an application in development), has its resources checked on every use.
     */
    boolean isStable();
}
