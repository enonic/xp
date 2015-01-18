package com.enonic.wem.api.node;

import java.time.Instant;

public class NodeVersion
    implements Comparable<NodeVersion>
{
    private final NodeVersionId nodeVersionId;

    private final Instant timestamp;

    public NodeVersion( final NodeVersionId nodeVersionId, final Instant timestamp )
    {
        this.nodeVersionId = nodeVersionId;
        this.timestamp = timestamp;
    }

    public NodeVersionId getId()
    {
        return nodeVersionId;
    }

    public Instant getTimestamp()
    {
        return timestamp;
    }


    // Insert with newest first
    @Override
    public int compareTo( final NodeVersion o )
    {
        if ( this.timestamp == o.timestamp )
        {
            return 0;
        }

        if ( this.timestamp.isBefore( o.timestamp ) )
        {
            return 1;
        }

        return -1;
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( !( o instanceof NodeVersion ) )
        {
            return false;
        }

        final NodeVersion that = (NodeVersion) o;

        if ( !nodeVersionId.equals( that.nodeVersionId ) )
        {
            return false;
        }
        if ( !timestamp.equals( that.timestamp ) )
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = nodeVersionId.hashCode();
        result = 31 * result + timestamp.hashCode();
        return result;
    }
}
