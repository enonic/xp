package com.enonic.wem.api.value;

import com.enonic.wem.api.entity.EntityId;

public final class EntityIdValue
    extends Value<EntityId>
{
    public EntityIdValue( final EntityId object )
    {
        super( ValueType.ENTITY_ID, object );
    }

    @Override
    public String asString()
    {
        return this.object.toString();
    }
}
