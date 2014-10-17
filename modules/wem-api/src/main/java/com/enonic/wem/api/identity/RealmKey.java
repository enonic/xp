package com.enonic.wem.api.identity;

public final class RealmKey
{
    private final String id;

    public RealmKey( final String id )
    {
        this.id = id;
    }

    @Override
    public String toString()
    {
        return this.id;
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( !( o instanceof RealmKey ) )
        {
            return false;
        }
        final RealmKey realmKey = (RealmKey) o;
        return id.equals( realmKey.id );
    }

    @Override
    public int hashCode()
    {
        return id.hashCode();
    }
}
