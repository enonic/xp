package com.enonic.xp.repo.impl.dump.upgrade.model8to9;

import java.text.Normalizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.repo.impl.NodeStoreVersion;
import com.enonic.xp.repo.impl.dump.serializer.json.VersionDumpEntryJson;
import com.enonic.xp.repo.impl.dump.upgrade.BranchEntryUpgrader;
import com.enonic.xp.repo.impl.dump.upgrade.NodeVersionEntryUpgrader;

/**
 * Trims node paths and normalizes them to Unicode NFC. Older versions accepted decomposed (NFD) node names,
 * e.g. {@code a} followed by combining ring above instead of {@code å}, which are no longer valid node names.
 */
public class NodePathNormalizeUpgrader
    implements NodeVersionEntryUpgrader, BranchEntryUpgrader
{
    private static final Logger LOG = LoggerFactory.getLogger( NodePathNormalizeUpgrader.class );

    @Override
    public VersionDumpEntryJson upgradeVersionEntry( final VersionDumpEntryJson versionEntry )
    {
        return normalizeNodePath( versionEntry );
    }

    @Override
    public VersionDumpEntryJson upgradeBranchMeta( final NodeStoreVersion nodeVersion, final VersionDumpEntryJson meta )
    {
        return normalizeNodePath( meta );
    }

    private VersionDumpEntryJson normalizeNodePath( final VersionDumpEntryJson entry )
    {
        final String nodePath = entry.getNodePath();
        if ( nodePath == null )
        {
            return entry;
        }

        final String trimmed = nodePath.trim();
        if ( !trimmed.equals( nodePath ) )
        {
            LOG.info( "Trimming nodePath [{}] to [{}]", nodePath, trimmed );
        }

        final String normalized = Normalizer.normalize( trimmed, Normalizer.Form.NFC );
        if ( !normalized.equals( trimmed ) )
        {
            LOG.warn( "Normalizing nodePath [{}] to Unicode NFC [{}]. URLs and references by path may change", trimmed, normalized );
        }

        if ( normalized.equals( nodePath ) )
        {
            return entry;
        }
        return VersionDumpEntryJson.create( entry ).nodePath( normalized ).build();
    }
}
