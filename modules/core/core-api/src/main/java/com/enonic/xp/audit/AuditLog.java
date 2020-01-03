package com.enonic.xp.audit;

import java.time.Instant;
import java.util.Objects;

import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.security.PrincipalKey;

import static java.util.Objects.requireNonNull;

public class AuditLog
{
    private final AuditLogId id;

    private final String type;

    private final Instant time;

    private final String source;

    private final PrincipalKey user;

    private final AuditLogUris objectUris;

    private final PropertyTree data;

    private AuditLog( final Builder builder )
    {
        id = requireNonNull( builder.id, "id is required for AuditLog" );
        type = requireNonNull( builder.type, "type is required for AuditLog" );
        time = requireNonNull( builder.time, "time is required for AuditLog" );
        source = requireNonNull( builder.source, "source is required for AuditLog" );
        user = requireNonNull( builder.user, "user is required for AuditLog" );
        objectUris = requireNonNull( builder.objectUris, "objectUris is required for AuditLog" );
        data = requireNonNull( builder.data, "data is required for AuditLog" );
    }

    public AuditLogId getId()
    {
        return id;
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
        final AuditLog auditLog = (AuditLog) o;
        return Objects.equals( id, auditLog.id ) && Objects.equals( type, auditLog.type ) && Objects.equals( time, auditLog.time ) &&
            Objects.equals( source, auditLog.source ) && Objects.equals( user, auditLog.user ) &&
            Objects.equals( objectUris, auditLog.objectUris ) && Objects.equals( data, auditLog.data );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( id, type, time, source, user, objectUris, data );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private AuditLogId id;

        private String type;

        private Instant time;

        private String source;

        private PrincipalKey user;

        private AuditLogUris objectUris;

        private PropertyTree data;

        public Builder id( final AuditLogId val )
        {
            id = val;
            return this;
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

        public AuditLog build()
        {
            return new AuditLog( this );
        }
    }
}
