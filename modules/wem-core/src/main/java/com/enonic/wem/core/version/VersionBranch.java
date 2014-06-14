package com.enonic.wem.core.version;

import java.util.Set;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.google.common.collect.UnmodifiableIterator;

import com.enonic.wem.api.blob.BlobKey;

public class VersionBranch
    implements Iterable<VersionEntry>
{
    private final ImmutableSet<VersionEntry> entries;

    private VersionBranch( final Builder builder )
    {
        this.entries = ImmutableSet.copyOf( builder.entries );
    }

    public int size()
    {
        return entries.size();
    }

    public boolean isEmpty()
    {
        return entries.isEmpty();
    }

    public VersionEntry getFirst()
    {
        return entries.iterator().next();
    }

    public boolean has( final VersionEntry version )
    {
        return entries.contains( version );
    }

    @Override
    public UnmodifiableIterator<VersionEntry> iterator()
    {
        return entries.iterator();
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
    {
        private Set<VersionEntry> entries = Sets.newLinkedHashSet();

        private Builder()
        {
        }

        public Builder set( final Set<VersionEntry> versionEntries )
        {
            this.entries = versionEntries;
            return this;
        }

        public Builder add( final String blobKey, final String parent )
        {
            this.entries.add( new VersionEntry( new BlobKey( blobKey ), Strings.isNullOrEmpty( parent ) ? null : new BlobKey( parent ) ) );
            return this;
        }

        public VersionBranch build()
        {
            return new VersionBranch( this );
        }

    }


}
