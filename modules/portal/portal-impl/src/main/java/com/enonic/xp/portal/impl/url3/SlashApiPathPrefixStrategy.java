package com.enonic.xp.portal.impl.url3;

import com.enonic.xp.portal.url.PathPrefixStrategy;

public class SlashApiPathPrefixStrategy
    implements PathPrefixStrategy
{
    @Override
    public String generatePathPrefix()
    {
        return "/api";
    }
}
