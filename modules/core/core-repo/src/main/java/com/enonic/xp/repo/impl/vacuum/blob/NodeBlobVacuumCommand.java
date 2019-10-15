package com.enonic.xp.repo.impl.vacuum.blob;

import com.enonic.xp.blob.SegmentLevel;
import com.enonic.xp.index.IndexPath;
import com.enonic.xp.repo.impl.node.NodeConstants;
import com.enonic.xp.repo.impl.version.VersionIndexPath;

public class NodeBlobVacuumCommand
    extends AbstractBlobVacuumCommand
{
    private NodeBlobVacuumCommand( final Builder builder )
    {
        super( builder );
    }

    public static Builder create()
    {
        return new Builder();
    }

    @Override
    protected SegmentLevel getBlobTypeSegmentLevel()
    {
        return NodeConstants.NODE_SEGMENT_LEVEL;
    }

    @Override
    protected IndexPath getFieldIndexPath()
    {
        return VersionIndexPath.NODE_BLOB_KEY;
    }

    public static final class Builder
        extends AbstractBlobVacuumCommand.Builder<Builder>
    {
        private Builder()
        {
        }

        public NodeBlobVacuumCommand build()
        {
            return new NodeBlobVacuumCommand( this );
        }
    }
}
