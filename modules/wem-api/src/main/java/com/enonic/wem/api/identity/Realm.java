package com.enonic.wem.api.identity;

public final class Realm
{
    private final RealmKey key;

    private final String name;

    public Realm( final Builder builder )
    {
        this.key = builder.key;
        this.name = builder.name;
    }

    public RealmKey getKey()
    {
        return key;
    }

    public String getName()
    {
        return name;
    }

    public static Builder newRealm()
    {
        return new Builder();
    }

    public static Builder newRealm( final Realm realm )
    {
        return new Builder( realm );
    }

    public static class Builder
    {
        private RealmKey key;

        private String name;

        private Builder()
        {
        }

        private Builder( final Realm realm )
        {
            this.key = realm.key;
            this.name = realm.name;
        }

        public Builder name( final String value )
        {
            this.name = value;
            return this;
        }

        public Builder key( final RealmKey value )
        {
            this.key = value;
            return this;
        }

        public Realm build()
        {
            return new Realm( this );
        }
    }

}
