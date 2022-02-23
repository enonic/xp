package com.enonic.xp.core.impl.app.resolver;

import java.util.Set;

import com.enonic.xp.resource.Resource;

public interface ApplicationUrlResolver
{
    Set<String> findFiles();

    Resource findResource( String path );
}
