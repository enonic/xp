package com.enonic.xp.repo.impl.dump.model;

import java.util.Collection;
import java.util.stream.Collectors;

import com.google.common.collect.Sets;

import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeVersionId;

public class DumpEntry
{
    private final NodeId nodeId;

    private final Collection<Meta> versions;

    private final Collection<String> binaryReferences;

    private DumpEntry( final Builder builder )
    {
        nodeId = builder.nodeId;
        versions = builder.versions;
        binaryReferences = builder.binaryReferences;
    }

    public NodeId getNodeId()
    {
        return nodeId;
    }

    public Collection<Meta> getVersions()
    {
        return versions;
    }

    public Collection<NodeVersionId> getAllVersionIds()
    {
        return versions.stream().map( Meta::getVersion ).collect( Collectors.toList() );
    }

    public Collection<String> getBinaryReferences()
    {
        return binaryReferences;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private NodeId nodeId;

        private Collection<Meta> versions = Sets.newHashSet();

        private Collection<String> binaryReferences = Sets.newHashSet();

        private Builder()
        {
        }

        public Builder nodeId( final NodeId val )
        {
            nodeId = val;
            return this;
        }

        public Builder setBinaryReferences( final Collection<String> references )
        {
            this.binaryReferences = references;
            return this;
        }

        public Builder addBinaryReferences( final Collection<String> references )
        {
            this.binaryReferences.addAll( references );
            return this;
        }

        public Builder setVersions( final Collection<Meta> values )
        {
            this.versions = values;
            return this;
        }

        public Builder addVersion( final Meta val )
        {
            versions.add( val );
            return this;
        }

        public DumpEntry build()
        {
            return new DumpEntry( this );
        }
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

        final DumpEntry dumpEntry = (DumpEntry) o;

        if ( nodeId != null ? !nodeId.equals( dumpEntry.nodeId ) : dumpEntry.nodeId != null )
        {
            return false;
        }
        if ( versions != null ? !versions.equals( dumpEntry.versions ) : dumpEntry.versions != null )
        {
            return false;
        }
        return binaryReferences != null ? binaryReferences.equals( dumpEntry.binaryReferences ) : dumpEntry.binaryReferences == null;

    }

    @Override
    public int hashCode()
    {
        int result = nodeId != null ? nodeId.hashCode() : 0;
        result = 31 * result + ( versions != null ? versions.hashCode() : 0 );
        result = 31 * result + ( binaryReferences != null ? binaryReferences.hashCode() : 0 );
        return result;
    }
}
