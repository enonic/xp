package com.enonic.xp.repo.impl.dump.upgrade.model8to9;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.repo.impl.NodeStoreVersion;
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
        return trimNodePath( versionEntry );
    }

    @Override
    public VersionDumpEntryJson upgradeBranchMeta( final NodeStoreVersion nodeVersion, final VersionDumpEntryJson meta )
    {
        return trimNodePath( meta );
    }

    private VersionDumpEntryJson trimNodePath( final VersionDumpEntryJson entry )
    {
        final String nodePath = entry.getNodePath();
        if ( nodePath == null )
        {
            return entry;
        }

        final String trimmed = nodePath.trim();
        if ( trimmed.equals( nodePath ) )
        {
            return entry;
        }

        LOG.info( "Trimming nodePath [{}] to [{}]", nodePath, trimmed );
        return VersionDumpEntryJson.create( entry ).nodePath( trimmed ).build();
    }
}
