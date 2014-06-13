package com.enonic.wem.core.version;

import java.util.Set;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.google.common.collect.UnmodifiableIterator;

import com.enonic.wem.api.blob.BlobKey;

public class VersionBranch
    implements Iterable<VersionBranch.Entry>
{

    private final ImmutableSet<Entry> entries;


    public VersionBranch( final Builder builder )
    {
        this.entries = ImmutableSet.copyOf( builder.entries );
    }

    @Override
    public UnmodifiableIterator<Entry> iterator()
    {
        return entries.iterator();
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
    {
        private Set<Entry> entries = Sets.newLinkedHashSet();

        private Builder()
        {

        }

        public Builder add( final String blobKey, final String parent )
        {
            this.entries.add( new Entry( new BlobKey( blobKey ), Strings.isNullOrEmpty( parent ) ? null : new BlobKey( parent ) ) );
            return this;
        }

        public VersionBranch build()
        {
            return new VersionBranch( this );
        }

    }

    public static class Entry
    {
        private BlobKey blobKey;

        private BlobKey parent;

        public Entry( final BlobKey blobKey, final BlobKey parent )
        {
            this.blobKey = blobKey;
            this.parent = parent;
        }
    }


}
