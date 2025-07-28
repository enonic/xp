package com.enonic.xp.audit;

import java.util.Collection;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.support.AbstractImmutableEntityList;

public final class AuditLogs
    extends AbstractImmutableEntityList<AuditLog>
{
    public static final AuditLogs EMPTY = new AuditLogs( ImmutableList.of() );

    private AuditLogs( final ImmutableList<AuditLog> list )
    {
        super( list );
    }

    public static AuditLogs empty()
    {
        return EMPTY;
    }

    public static AuditLogs from( final AuditLog... auditLogs )
    {
        return fromInternal( ImmutableList.copyOf( auditLogs ) );
    }

    public static AuditLogs from( final Iterable<? extends AuditLog> auditLogs )
    {
        return auditLogs instanceof AuditLogs a ? a : fromInternal( ImmutableList.copyOf( auditLogs ) );
    }

    public static Collector<AuditLog, ?, AuditLogs> collector()
    {
        return Collectors.collectingAndThen( ImmutableList.toImmutableList(), AuditLogs::fromInternal );
    }

    private static AuditLogs fromInternal( final ImmutableList<AuditLog> list )
    {
        return list.isEmpty() ? EMPTY : new AuditLogs( list );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public AuditLog getAuditLogById( final AuditLogId auditLogId )
    {
        return this.list.stream().filter( al -> auditLogId.equals( al.getId() ) )
            .findFirst()
            .orElse( null );
    }

    public static final class Builder
    {
        private final ImmutableList.Builder<AuditLog> auditLogs = ImmutableList.builder();

        public Builder add( final AuditLog auditLog )
        {
            auditLogs.add( auditLog );
            return this;
        }

        public Builder addAll( final Iterable<? extends AuditLog> auditLogs )
        {
            this.auditLogs.addAll( auditLogs );
            return this;
        }

        public AuditLogs build()
        {
            return fromInternal( auditLogs.build() );
        }
    }
}
