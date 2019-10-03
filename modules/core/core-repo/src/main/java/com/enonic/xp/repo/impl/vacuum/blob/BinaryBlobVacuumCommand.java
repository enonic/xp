package com.enonic.xp.repo.impl.vacuum.blob;

import com.enonic.xp.blob.SegmentLevel;
import com.enonic.xp.index.IndexPath;
import com.enonic.xp.repo.impl.node.NodeConstants;
import com.enonic.xp.repo.impl.version.VersionIndexPath;

public class BinaryBlobVacuumCommand
    extends AbstractBlobVacuumCommand
{
    private BinaryBlobVacuumCommand( final Builder builder )
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
        return NodeConstants.BINARY_SEGMENT_LEVEL;
    }

    @Override
    protected IndexPath getFieldIndexPath()
    {
        return VersionIndexPath.BINARY_BLOB_KEYS;
    }

    public static final class Builder
        extends AbstractBlobVacuumCommand.Builder<Builder>
    {
        private Builder()
        {
        }

        public BinaryBlobVacuumCommand build()
        {
            return new BinaryBlobVacuumCommand( this );
        }
    }
}
