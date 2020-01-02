package com.enonic.xp.audit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableSet;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.support.AbstractImmutableEntitySet;

@PublicApi
public final class AuditLogIds
    extends AbstractImmutableEntitySet<AuditLogId>
    implements Iterable<AuditLogId>
{
    private AuditLogIds( final ImmutableSet<AuditLogId> set )
    {
        super( set );
    }

    public static AuditLogIds empty()
    {
        final ImmutableSet<AuditLogId> set = ImmutableSet.of();
        return new AuditLogIds( set );
    }

    public static AuditLogIds from( final AuditLogId... ids )
    {
        return new AuditLogIds( ImmutableSet.copyOf( ids ) );
    }

    public static AuditLogIds from( final String... ids )
    {
        return new AuditLogIds( parseIds( ids ) );
    }

    public static AuditLogIds from( final Collection<String> ids )
    {
        return new AuditLogIds( doParseIds( ids ) );
    }

    public static AuditLogIds from( final Iterable<AuditLogId> ids )
    {
        return new AuditLogIds( ImmutableSet.copyOf( ids ) );
    }

    private static ImmutableSet<AuditLogId> parseIds( final String... ids )
    {
        return doParseIds( Arrays.asList( ids ) );
    }

    private static ImmutableSet<AuditLogId> doParseIds( final Collection<String> list )
    {
        return list.stream().map( AuditLogId::from ).collect( ImmutableSet.toImmutableSet() );
    }

    public Set<String> asStrings()
    {
        return this.set.stream().map( AuditLogId::toString ).collect( Collectors.toSet() );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
    {
        private List<AuditLogId> contents = new ArrayList<>();

        public Builder add( final AuditLogId auditLogId )
        {
            this.contents.add( auditLogId );
            return this;
        }

        public Builder addAll( final AuditLogIds auditLogIds )
        {
            this.contents.addAll( auditLogIds.getSet() );
            return this;
        }


        public AuditLogIds build()
        {
            return AuditLogIds.from( contents );
        }
    }
}
