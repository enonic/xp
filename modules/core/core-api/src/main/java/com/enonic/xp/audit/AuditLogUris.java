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
public final class AuditLogUris
    extends AbstractImmutableEntitySet<AuditLogUri>
    implements Iterable<AuditLogUri>
{
    private AuditLogUris( final ImmutableSet<AuditLogUri> set )
    {
        super( set );
    }

    public static AuditLogUris empty()
    {
        final ImmutableSet<AuditLogUri> set = ImmutableSet.of();
        return new AuditLogUris( set );
    }

    public static AuditLogUris from( final AuditLogUri... uris )
    {
        return new AuditLogUris( ImmutableSet.copyOf( uris ) );
    }

    public static AuditLogUris from( final String... uris )
    {
        return new AuditLogUris( parseIds( uris ) );
    }

    public static AuditLogUris from( final Collection<String> uris )
    {
        return new AuditLogUris( doParseIds( uris ) );
    }

    public static AuditLogUris from( final Iterable<AuditLogUri> uris )
    {
        return new AuditLogUris( ImmutableSet.copyOf( uris ) );
    }

    private static ImmutableSet<AuditLogUri> parseIds( final String... uris )
    {
        return doParseIds( Arrays.asList( uris ) );
    }

    private static ImmutableSet<AuditLogUri> doParseIds( final Collection<String> list )
    {
        return list.stream().map( AuditLogUri::from ).collect( ImmutableSet.toImmutableSet() );
    }

    public Set<String> asStrings()
    {
        return this.set.stream().map( AuditLogUri::toString ).collect( Collectors.toSet() );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
    {
        private List<AuditLogUri> contents = new ArrayList<>();

        public Builder add( final AuditLogUri auditLogUri )
        {
            this.contents.add( auditLogUri );
            return this;
        }

        public Builder addAll( final AuditLogUris auditLogUris )
        {
            this.contents.addAll( auditLogUris.getSet() );
            return this;
        }


        public AuditLogUris build()
        {
            return AuditLogUris.from( contents );
        }
    }
}
