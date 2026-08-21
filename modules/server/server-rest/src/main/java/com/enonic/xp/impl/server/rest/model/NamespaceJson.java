package com.enonic.xp.impl.server.rest.model;

import com.enonic.xp.app.Namespace;

public class NamespaceJson
{
    private final String key;

    private final String description;

    public NamespaceJson( final Namespace namespace )
    {
        this.key = namespace.getKey().toString();
        this.description = namespace.getDescription();
    }

    public String getKey()
    {
        return key;
    }

    public String getDescription()
    {
        return description;
    }
}
