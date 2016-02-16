package com.enonic.xp.admin.impl.rest.resource.security.json;


import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.security.PathGuard;

public final class PathGuardsJson
{
    private final List<PathGuardJson> pathGuardsJsons;

    public PathGuardsJson( final ImmutableList<PathGuard> pathGuards )
    {
        this.pathGuardsJsons = new ArrayList<>( pathGuards.size() );
        for ( PathGuard pathGuard : pathGuards )
        {
            pathGuardsJsons.add( new PathGuardJson( pathGuard ) );
        }

    }

    public List<PathGuardJson> getPathGuards()
    {
        return pathGuardsJsons;
    }
}
