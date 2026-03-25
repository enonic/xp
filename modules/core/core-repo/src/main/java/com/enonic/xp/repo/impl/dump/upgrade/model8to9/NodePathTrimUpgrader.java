package com.enonic.xp.repo.impl.dump.upgrade.model8to9;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.repo.impl.dump.serializer.json.BranchDumpEntryJson;
import com.enonic.xp.repo.impl.dump.serializer.json.VersionDumpEntryJson;
import com.enonic.xp.repo.impl.dump.upgrade.BranchEntryUpgrader;
import com.enonic.xp.repo.impl.dump.upgrade.NodeVersionEntryUpgrader;

public class NodePathTrimUpgrader
    implements NodeVersionEntryUpgrader, BranchEntryUpgrader
{
    private static final Logger LOG = LoggerFactory.getLogger( NodePathTrimUpgrader.class );

    @Override
    public VersionDumpEntryJson upgradeVersionEntry( final VersionDumpEntryJson versionEntry )
    {
        final VersionDumpEntryJson result = doUpgradeVersionEntry( versionEntry );
        if ( result != versionEntry )
        {
            LOG.info( "Trimming nodePath [{}] to [{}]", versionEntry.getNodePath(), result.getNodePath() );
        }
        return result;
    }

    @Override
    public BranchDumpEntryJson upgradeBranchEntry( final BranchDumpEntryJson branchEntry )
    {
        final VersionDumpEntryJson meta = branchEntry.getMeta();
        final VersionDumpEntryJson upgradedMeta = doUpgradeVersionEntry( meta );
        if ( upgradedMeta == meta )
        {
            return branchEntry;
        }
        LOG.info( "Trimming nodePath [{}] to [{}]", meta.getNodePath(), upgradedMeta.getNodePath() );
        return BranchDumpEntryJson.create( branchEntry ).meta( upgradedMeta ).build();
    }

    private VersionDumpEntryJson doUpgradeVersionEntry( final VersionDumpEntryJson versionEntry )
    {
        final String nodePath = versionEntry.getNodePath();
        if ( nodePath == null )
        {
            return versionEntry;
        }

        final String trimmed = nodePath.trim();
        if ( trimmed.equals( nodePath ) )
        {
            return versionEntry;
        }

        return VersionDumpEntryJson.create( versionEntry ).nodePath( trimmed ).build();
    }
}
