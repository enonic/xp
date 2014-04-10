package com.enonic.wem.core.entity.json;

import com.fasterxml.jackson.annotation.JsonIgnore;

import com.enonic.wem.api.entity.Entity;

public class EntityJson
    extends AbstractEntityJson
{
    private final Entity entity;

    public EntityJson( final Entity entity )
    {
        super( entity );
        this.entity = entity;
    }

    @JsonIgnore
    public Entity getEntity()
    {
        return entity;
    }
}
