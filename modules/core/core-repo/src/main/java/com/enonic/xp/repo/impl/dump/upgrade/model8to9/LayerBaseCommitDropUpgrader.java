package com.enonic.xp.repo.impl.dump.upgrade.model8to9;

import java.util.Set;

import com.enonic.xp.repo.impl.dump.serializer.json.CommitDumpEntryJson;
import com.enonic.xp.repo.impl.dump.serializer.json.VersionDumpEntryJson;

/**
 * Pure functions used by {@link DumpUpgrader8to9} to drop the synthetic
 * "Base inherited version" commits that XP 7 created when a layered content was
 * localized (de-inherited), and to clear every dangling reference to those
 * commits from version and branch metas.
 * <p>
 * Coordination state — the set of dropped commit ids — is owned by the caller
 * (populated during the commits phase, consulted during the versions/branch
 * phases).
 */
public final class LayerBaseCommitDropUpgrader
{
    static final String BASE_INHERITED_VERSION_MESSAGE = "Base inherited version";

    private LayerBaseCommitDropUpgrader()
    {
    }

    public static boolean isLayerBaseCommit( final CommitDumpEntryJson commitEntry )
    {
        return BASE_INHERITED_VERSION_MESSAGE.equals( commitEntry.getMessage() );
    }

    public static VersionDumpEntryJson clearDroppedCommitId( final Set<String> droppedCommitIds, final VersionDumpEntryJson entry )
    {
        if ( entry == null || entry.getCommitId() == null || !droppedCommitIds.contains( entry.getCommitId() ) )
        {
            return entry;
        }
        return VersionDumpEntryJson.create( entry ).commitId( null ).build();
    }

}
