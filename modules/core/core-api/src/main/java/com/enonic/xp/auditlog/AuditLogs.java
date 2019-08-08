package com.enonic.xp.auditlog;

import java.util.Collection;
import java.util.Set;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import com.enonic.xp.support.AbstractImmutableEntitySet;

public final class AuditLogs
    extends AbstractImmutableEntitySet<AuditLog>
{
    private final ImmutableMap<AuditLogId, AuditLog> map;

    private AuditLogs( final Set<AuditLog> set )
    {
        super( ImmutableSet.copyOf( set ) );
        this.map = Maps.uniqueIndex( set, input -> input.getId() );
    }

    public static AuditLogs empty()
    {
        final ImmutableSet<AuditLog> set = ImmutableSet.of();
        return new AuditLogs( set );
    }

    public static AuditLogs from( final AuditLog... auditLogs )
    {
        return new AuditLogs( ImmutableSet.copyOf( auditLogs ) );
    }

    public static AuditLogs from( final Iterable<? extends AuditLog> auditLogs )
    {
        return new AuditLogs( ImmutableSet.copyOf( auditLogs ) );
    }

    public static AuditLogs from( final Collection<? extends AuditLog> auditLogs )
    {
        return new AuditLogs( ImmutableSet.copyOf( auditLogs ) );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public AuditLog getAuditLogById( final AuditLogId AuditLogId )
    {
        return this.map.get( AuditLogId );
    }

    public static class Builder
    {
        private final Set<AuditLog> auditLogs = Sets.newLinkedHashSet();

        public Builder add( AuditLog auditLog )
        {
            auditLogs.add( auditLog );
            return this;
        }

        public Builder addAll( AuditLogs auditLogs )
        {
            this.auditLogs.addAll( auditLogs.getSet() );
            return this;
        }

        public AuditLogs build()
        {
            return new AuditLogs( auditLogs );
        }
    }
}
