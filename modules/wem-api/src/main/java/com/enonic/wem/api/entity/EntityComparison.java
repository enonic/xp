package com.enonic.wem.api.entity;

public class EntityComparison
{
    private final EntityId entityId;

    private final CompareStatus compareStatus;

    public EntityComparison( final EntityId entityId, final CompareStatus compareStatus )
    {
        this.entityId = entityId;
        this.compareStatus = compareStatus;
    }

    public EntityId getEntityId()
    {
        return entityId;
    }

    public CompareStatus getCompareStatus()
    {
        return compareStatus;
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

        if ( compareStatus != null ? !compareStatus.equals( that.compareStatus ) : that.compareStatus != null )
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
        result = 31 * result + ( compareStatus != null ? compareStatus.hashCode() : 0 );
        return result;
    }
}
