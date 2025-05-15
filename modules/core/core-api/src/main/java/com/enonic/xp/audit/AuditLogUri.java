package com.enonic.xp.audit;

import java.util.Objects;

import static java.util.Objects.requireNonNull;

public final class AuditLogUri
{
    private final String value;

    private AuditLogUri( final Builder builder )
    {
        requireNonNull( builder.value, "value is required for AuditLogUri" );
        value = builder.value;
    }

    public String getValue()
    {
        return value;
    }

    @Override
    public String toString()
    {
        return value;
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }
        final AuditLogUri that = (AuditLogUri) o;
        return Objects.equals( value, that.value );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( value );
    }

    public static AuditLogUri from( final String value )
    {
        return create().
            value( value ).
            build();
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private String value;

        private Builder()
        {
        }

        public Builder value( final String value )
        {
            this.value = value;
            return this;
        }

        public AuditLogUri build()
        {
            return new AuditLogUri( this );
        }
    }
}
