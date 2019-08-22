package com.enonic.xp.audit;

import java.net.URI;

import static java.util.Objects.requireNonNull;

public class AuditLogUri
{
    private URI value;

    private AuditLogUri( final Builder builder )
    {
        requireNonNull( builder.value, "value is required for AuditLogUri" );
        value = builder.value;
    }

    public URI getValue()
    {
        return value;
    }

    @Override
    public String toString()
    {
        return value.toString();
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
        private URI value;

        private Builder()
        {
        }

        public Builder value( final String value )
        {
            this.value = URI.create( value );
            return this;
        }

        public AuditLogUri build()
        {
            return new AuditLogUri( this );
        }
    }
}
