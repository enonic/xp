package com.enonic.xp.repo.impl.dump.upgrade.model8to9;

import org.junit.jupiter.api.Test;

import com.enonic.xp.repo.impl.dump.serializer.json.VersionDumpEntryJson;

import static org.assertj.core.api.Assertions.assertThat;

class NodePathTrimUpgraderTest
{
    private final NodePathTrimUpgrader upgrader = new NodePathTrimUpgrader();

    @Test
    void nodePath_with_trailing_whitespace_is_trimmed()
    {
        final VersionDumpEntryJson entry = createEntry( "/content/my-node " );

        final VersionDumpEntryJson result = upgrader.upgradeVersionEntry( entry );

        assertThat( result.getNodePath() ).isEqualTo( "/content/my-node" );
    }

    @Test
    void nodePath_with_leading_whitespace_is_trimmed()
    {
        final VersionDumpEntryJson entry = createEntry( " /content/my-node" );

        final VersionDumpEntryJson result = upgrader.upgradeVersionEntry( entry );

        assertThat( result.getNodePath() ).isEqualTo( "/content/my-node" );
    }

    @Test
    void nodePath_without_whitespace_is_unchanged()
    {
        final VersionDumpEntryJson entry = createEntry( "/content/my-node" );

        final VersionDumpEntryJson result = upgrader.upgradeVersionEntry( entry );

        assertThat( result ).isSameAs( entry );
    }

    @Test
    void null_nodePath_is_unchanged()
    {
        final VersionDumpEntryJson entry = createEntry( null );

        final VersionDumpEntryJson result = upgrader.upgradeVersionEntry( entry );

        assertThat( result ).isSameAs( entry );
    }

    @Test
    void branchMeta_nodePath_with_whitespace_is_trimmed()
    {
        final VersionDumpEntryJson meta = createEntry( "/content/my-node " );

        final VersionDumpEntryJson result = upgrader.upgradeBranchMeta( null, meta );

        assertThat( result.getNodePath() ).isEqualTo( "/content/my-node" );
    }

    @Test
    void branchMeta_nodePath_without_whitespace_is_unchanged()
    {
        final VersionDumpEntryJson meta = createEntry( "/content/my-node" );

        final VersionDumpEntryJson result = upgrader.upgradeBranchMeta( null, meta );

        assertThat( result ).isSameAs( meta );
    }

    private static VersionDumpEntryJson createEntry( final String nodePath )
    {
        return VersionDumpEntryJson.create()
            .nodePath( nodePath )
            .nodeBlobKey( "abc" )
            .indexConfigBlobKey( "def" )
            .accessControlBlobKey( "ghi" )
            .build();
    }
}
