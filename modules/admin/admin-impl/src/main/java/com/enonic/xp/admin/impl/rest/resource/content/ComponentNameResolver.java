package com.enonic.xp.admin.impl.rest.resource.content;


import com.enonic.xp.region.Component;
import com.enonic.xp.region.ComponentName;

public interface ComponentNameResolver
{
    ComponentName resolve( final Component component );
}