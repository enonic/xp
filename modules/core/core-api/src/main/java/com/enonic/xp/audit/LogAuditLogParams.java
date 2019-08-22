package com.enonic.xp.audit;

import java.net.URI;
import java.time.Instant;
import java.util.Objects;

import com.google.common.collect.ImmutableSet;

import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.User;

public class LogAuditLogParams
{
    private final String type;

    private final Instant time;

    private final String source;

    private final PrincipalKey user;

    private final String message;

    private final AuditLogUris objectUris;

    private final PropertyTree data;

    private LogAuditLogParams( final Builder builder )
    {
        type = Objects.requireNonNull( builder.type, "AuditLogParams type cannot be null" );
        time = Objects.requireNonNullElseGet( builder.time, () -> Instant.now() );
        source = Objects.requireNonNullElse( builder.source, "" );
        user = Objects.requireNonNullElseGet( builder.user, () -> getUserKey() );
        message = Objects.requireNonNullElse( builder.message, "" );
        objectUris = Objects.requireNonNullElse( builder.objectUris, AuditLogUris.empty() );
        data = Objects.requireNonNullElse( builder.data, new PropertyTree() );
    }

    private PrincipalKey getUserKey()
    {
        final Context context = ContextAccessor.current();
        final User user = context.getAuthInfo().getUser() != null ? context.getAuthInfo().getUser() : User.ANONYMOUS;
        return user.getKey();
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

    public String getMessage()
    {
        return message;
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

    public static final class Builder
    {
        private String type;

        private Instant time;

        private String source;

        private PrincipalKey user;

        private String message;

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

        public Builder message( final String val )
        {
            message = val;
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
