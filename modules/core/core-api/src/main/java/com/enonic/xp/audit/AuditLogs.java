package com.enonic.xp.audit;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Function;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import com.enonic.xp.support.AbstractImmutableEntitySet;

public final class AuditLogs
    extends AbstractImmutableEntitySet<AuditLog>
{
    private final ImmutableMap<AuditLogId, AuditLog> map;

    private AuditLogs( final Set<AuditLog> set )
    {
        super( ImmutableSet.copyOf( set ) );
        this.map = set.stream().collect( ImmutableMap.toImmutableMap( AuditLog::getId, Function.identity() ) );
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

    public AuditLog getAuditLogById( final AuditLogId auditLogId )
    {
        return this.map.get( auditLogId );
    }

    public static class Builder
    {
        private final Set<AuditLog> auditLogs = new LinkedHashSet<>();

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
