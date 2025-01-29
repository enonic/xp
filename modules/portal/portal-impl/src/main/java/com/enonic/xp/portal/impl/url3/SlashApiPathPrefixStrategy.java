package com.enonic.xp.portal.impl.url3;

public class SlashApiPathPrefixStrategy
    implements PathPrefixStrategy
{
    @Override
    public String generatePathPrefix()
    {
        return "/api";
    }
}
