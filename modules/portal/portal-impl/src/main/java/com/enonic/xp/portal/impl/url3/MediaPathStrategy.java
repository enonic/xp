package com.enonic.xp.portal.impl.url3;

import com.enonic.xp.portal.url.PathPrefixStrategy;
import com.enonic.xp.portal.url.PathStrategy;

public class MediaPathStrategy
    implements PathStrategy
{
    private final PathPrefixStrategy pathPrefixStrategy;

    private final PathStrategy pathStrategy;

    public MediaPathStrategy( final PathPrefixStrategy pathPrefixStrategy, final PathStrategy pathStrategy )
    {
        this.pathPrefixStrategy = pathPrefixStrategy;
        this.pathStrategy = pathStrategy;
    }

    @Override
    public String generatePath()
    {
        final String pathPrefix = this.pathPrefixStrategy.generatePathPrefix();
        final String path = this.pathStrategy.generatePath();
        return pathPrefix + path;
    }
}
