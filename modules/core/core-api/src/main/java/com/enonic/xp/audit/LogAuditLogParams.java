package com.enonic.xp.audit;

import java.time.Instant;
import java.util.Objects;

import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.security.PrincipalKey;

public final class LogAuditLogParams
{
    private final String type;

    private final Instant time;

    private final String source;

    private final PrincipalKey user;

    private final AuditLogUris objectUris;

    private final PropertyTree data;

    private LogAuditLogParams( final Builder builder )
    {
        type = Objects.requireNonNull( builder.type, "LogAuditLogParams type cannot be null" );
        time = Objects.requireNonNullElseGet( builder.time, Instant::now );
        source = Objects.requireNonNullElse( builder.source, "" );
        user = builder.user;
        objectUris = Objects.requireNonNullElse( builder.objectUris, AuditLogUris.empty() );
        data = Objects.requireNonNullElse( builder.data, new PropertyTree() );
    }

    public String getType()
    {
        return type;
    }

    public Instant getTime()
    {
        return time;
    }

    public String getSource()
    {
        return source;
    }

    public PrincipalKey getUser()
    {
        return user;
    }

    public AuditLogUris getObjectUris()
    {
        return objectUris;
    }

    public PropertyTree getData()
    {
        return data;
    }

    public static Builder create()
    {
        return new Builder();
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
        final LogAuditLogParams that = (LogAuditLogParams) o;
        return Objects.equals( type, that.type ) && Objects.equals( time, that.time ) && Objects.equals( source, that.source ) &&
            Objects.equals( user, that.user ) && Objects.equals( objectUris, that.objectUris ) && Objects.equals( data, that.data );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( type, time, source, user, objectUris, data );
    }

    public static final class Builder
    {
        private String type;

        private Instant time;

        private String source;

        private PrincipalKey user;

        private AuditLogUris objectUris;

        private PropertyTree data;

        private Builder()
        {
        }

        public Builder type( final String val )
        {
            type = val;
            return this;
        }

        public Builder time( final Instant val )
        {
            time = val;
            return this;
        }

        public Builder source( final String val )
        {
            source = val;
            return this;
        }

        public Builder user( final PrincipalKey val )
        {
            user = val;
            return this;
        }

        public Builder objectUris( final AuditLogUris val )
        {
            objectUris = val;
            return this;
        }

        public Builder data( final PropertyTree val )
        {
            data = val;
            return this;
        }

        public LogAuditLogParams build()
        {
            return new LogAuditLogParams( this );
        }
    }
}
