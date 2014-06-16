package com.enonic.wem.api.entity;

public class EntityComparison
{
    private final EntityId entityId;

    private final CompareState compareState;

    public EntityComparison( final EntityId entityId, final CompareState compareState )
    {
        this.entityId = entityId;
        this.compareState = compareState;
    }

    public EntityId getEntityId()
    {
        return entityId;
    }

    public CompareState getCompareState()
    {
        return compareState;
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( !( o instanceof EntityComparison ) )
        {
            return false;
        }

        final EntityComparison that = (EntityComparison) o;

        if ( compareState != null ? !compareState.equals( that.compareState ) : that.compareState != null )
        {
            return false;
        }
        if ( entityId != null ? !entityId.equals( that.entityId ) : that.entityId != null )
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = entityId != null ? entityId.hashCode() : 0;
        result = 31 * result + ( compareState != null ? compareState.hashCode() : 0 );
        return result;
    }
}
