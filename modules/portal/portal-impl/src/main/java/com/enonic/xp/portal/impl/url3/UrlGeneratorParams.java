package com.enonic.xp.portal.impl.url3;

import java.util.Objects;

import com.enonic.xp.portal.url.BaseUrlStrategy;
import com.enonic.xp.portal.url.PathStrategy;
import com.enonic.xp.portal.url.RewritePathStrategy;

public class UrlGeneratorParams
{
    protected final BaseUrlStrategy baseUrlStrategy;

    protected final PathStrategy pathStrategy;

    protected final RewritePathStrategy rewritePathStrategy;

    protected UrlGeneratorParams( final BaseUrlStrategy baseUrlStrategy, final PathStrategy pathStrategy,
                                  final RewritePathStrategy rewritePathStrategy )
    {
        this.baseUrlStrategy = Objects.requireNonNull( baseUrlStrategy );
        this.pathStrategy = Objects.requireNonNull( pathStrategy );
        this.rewritePathStrategy = Objects.requireNonNull( rewritePathStrategy );
    }

    public BaseUrlStrategy getBaseUrlStrategy()
    {
        return baseUrlStrategy;
    }

    public PathStrategy getPathStrategy()
    {
        return pathStrategy;
    }

    public RewritePathStrategy getRewritePathStrategy()
    {
        return rewritePathStrategy;
    }
}
