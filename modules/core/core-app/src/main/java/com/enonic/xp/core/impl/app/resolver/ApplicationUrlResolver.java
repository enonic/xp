package com.enonic.xp.core.impl.app.resolver;

import java.net.URL;
import java.util.Set;

public interface ApplicationUrlResolver
{
    Set<String> findFiles();

    URL findUrl( String path );
}
