package com.enonic.xp.portal.impl.url2;

import java.util.Objects;

public record UrlGeneratorParams(PathGenerator pathGenerator, PathRewriter pathRewriter, BaseUrlResolver baseUrlResolver)
{

    public UrlGeneratorParams( PathGenerator pathGenerator, PathRewriter pathRewriter, BaseUrlResolver baseUrlResolver )
    {
        this.pathGenerator = Objects.requireNonNull( pathGenerator );
        this.pathRewriter = Objects.requireNonNull( pathRewriter );
        this.baseUrlResolver = Objects.requireNonNull( baseUrlResolver );
    }
}
