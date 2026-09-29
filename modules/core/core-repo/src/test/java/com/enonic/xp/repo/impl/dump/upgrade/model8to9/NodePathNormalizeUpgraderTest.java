package com.enonic.xp.repo.impl.dump.upgrade.model8to9;

import org.junit.jupiter.api.Test;

import com.enonic.xp.repo.impl.dump.serializer.json.VersionDumpEntryJson;

import static org.assertj.core.api.Assertions.assertThat;

class NodePathNormalizeUpgraderTest
{
    private final NodePathNormalizeUpgrader upgrader = new NodePathNormalizeUpgrader();

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

    @Test
    void decomposed_nodePath_is_normalized_to_nfc()
    {
        final VersionDumpEntryJson entry = createEntry( "/content/bla\u030Ab\u00E6r/ga\u030Ard" );

        final VersionDumpEntryJson result = upgrader.upgradeVersionEntry( entry );

        assertThat( result.getNodePath() ).isEqualTo( "/content/bl\u00E5b\u00E6r/g\u00E5rd" );
    }

    @Test
    void decomposed_nodePath_with_whitespace_is_trimmed_and_normalized()
    {
        final VersionDumpEntryJson entry = createEntry( " /content/a\u030A " );

        final VersionDumpEntryJson result = upgrader.upgradeVersionEntry( entry );

        assertThat( result.getNodePath() ).isEqualTo( "/content/\u00E5" );
    }

    @Test
    void composed_nodePath_is_unchanged()
    {
        final VersionDumpEntryJson entry = createEntry( "/content/bl\u00E5b\u00E6r" );

        final VersionDumpEntryJson result = upgrader.upgradeVersionEntry( entry );

        assertThat( result ).isSameAs( entry );
    }

    @Test
    void branchMeta_decomposed_nodePath_is_normalized_to_nfc()
    {
        final VersionDumpEntryJson meta = createEntry( "/content/a\u030A" );

        final VersionDumpEntryJson result = upgrader.upgradeBranchMeta( null, meta );

        assertThat( result.getNodePath() ).isEqualTo( "/content/\u00E5" );
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
