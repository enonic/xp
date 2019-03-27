package com.enonic.xp.repo.impl.dump.upgrade;

import java.nio.file.Path;

import com.enonic.xp.repo.impl.dump.upgrade.obsoletemodel.pre2.Pre2BranchDumpEntryJson;
import com.enonic.xp.repo.impl.dump.upgrade.obsoletemodel.pre2.Pre2VersionDumpEntryJson;
import com.enonic.xp.repo.impl.dump.upgrade.obsoletemodel.pre2.Pre2VersionsDumpEntryJson;
import com.enonic.xp.repo.impl.dump.upgrade.obsoletemodel.pre4.Pre4BranchDumpEntryJson;
import com.enonic.xp.repo.impl.dump.upgrade.obsoletemodel.pre4.Pre4VersionDumpEntryJson;
import com.enonic.xp.repo.impl.dump.upgrade.obsoletemodel.pre4.Pre4VersionsDumpEntryJson;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.util.Version;

public class VersionIdDumpUpgrader
    extends AbstractMetaDumpUpgrader
{
    private static final Version MODEL_VERSION = new Version( 2, 0, 0 );

    public VersionIdDumpUpgrader( final Path basePath )
    {
        super( basePath );
    }

    @Override
    public Version getModelVersion()
    {
        return MODEL_VERSION;
    }

    @Override
    protected String upgradeVersionEntry( final RepositoryId repositoryId, final String entryContent )
    {
        final Pre2VersionsDumpEntryJson sourceEntry = deserializeValue( entryContent, Pre2VersionsDumpEntryJson.class );
        final Pre4VersionsDumpEntryJson.Builder upgradedEntry = Pre4VersionsDumpEntryJson.create().
            nodeId( sourceEntry.getNodeId() );

        sourceEntry.getVersions().
            stream().
            map( this::upgradeVersion ).
            forEach( upgradedEntry::version );

        return serialize( upgradedEntry.build() );
    }

    @Override
    protected String upgradeBranchEntry( final RepositoryId repositoryId, final String entryContent )
    {
        final Pre2BranchDumpEntryJson sourceEntry = deserializeValue( entryContent, Pre2BranchDumpEntryJson.class );

        final Pre4BranchDumpEntryJson upgradedEntry = Pre4BranchDumpEntryJson.create().
            nodeId( sourceEntry.getNodeId() ).
            meta( upgradeVersion( sourceEntry.getMeta() ) ).
            binaries( sourceEntry.getBinaries() ).
            build();

        return serialize( upgradedEntry );
    }

    private Pre4VersionDumpEntryJson upgradeVersion( final Pre2VersionDumpEntryJson version )
    {
        return Pre4VersionDumpEntryJson.create().
            version( version.getVersion() ).
            blobKey( version.getVersion() ).
            nodePath( version.getNodePath() ).
            nodeState( version.getNodeState() ).
            timestamp( version.getTimestamp() ).
            build();
    }
}
