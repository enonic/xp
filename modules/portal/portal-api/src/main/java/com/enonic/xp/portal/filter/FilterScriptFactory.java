package com.enonic.xp.portal.filter;

import com.enonic.xp.resource.ResourceKey;

public interface FilterScriptFactory
{
    FilterScript fromScript( ResourceKey script );
}
