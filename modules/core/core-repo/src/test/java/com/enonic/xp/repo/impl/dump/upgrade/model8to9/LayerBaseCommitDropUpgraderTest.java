package com.enonic.xp.repo.impl.dump.upgrade.model8to9;

import java.util.Set;

import org.junit.jupiter.api.Test;

import com.enonic.xp.repo.impl.dump.serializer.json.CommitDumpEntryJson;
import com.enonic.xp.repo.impl.dump.serializer.json.VersionDumpEntryJson;

import static com.enonic.xp.repo.impl.dump.upgrade.model8to9.LayerBaseCommitDropUpgrader.BASE_INHERITED_VERSION_MESSAGE;
import static org.assertj.core.api.Assertions.assertThat;

class LayerBaseCommitDropUpgraderTest
{
    @Test
    void isLayerBaseCommit_true_for_base_inherited_message()
    {
        assertThat( LayerBaseCommitDropUpgrader.isLayerBaseCommit( commitEntry( "commit-base", BASE_INHERITED_VERSION_MESSAGE ) ) ).isTrue();
    }

    @Test
    void isLayerBaseCommit_false_for_other_messages()
    {
        assertThat( LayerBaseCommitDropUpgrader.isLayerBaseCommit( commitEntry( "commit-publish", "some publish message" ) ) ).isFalse();
    }

    @Test
    void version_referencing_dropped_commit_has_commitId_cleared()
    {
        final VersionDumpEntryJson entry = entryWithCommitId( "commit-base" );

        final VersionDumpEntryJson result = LayerBaseCommitDropUpgrader.clearDroppedCommitId( Set.of( "commit-base" ), entry );

        assertThat( result.getCommitId() ).isNull();
        assertThat( result.getNodePath() ).isEqualTo( entry.getNodePath() );
        assertThat( result.getNodeBlobKey() ).isEqualTo( entry.getNodeBlobKey() );
    }

    @Test
    void version_referencing_unrelated_commit_is_unchanged()
    {
        final VersionDumpEntryJson entry = entryWithCommitId( "commit-publish" );

        final VersionDumpEntryJson result = LayerBaseCommitDropUpgrader.clearDroppedCommitId( Set.of( "commit-base" ), entry );

        assertThat( result ).isSameAs( entry );
    }

    @Test
    void version_without_commitId_is_unchanged()
    {
        final VersionDumpEntryJson entry = entryWithCommitId( null );

        final VersionDumpEntryJson result = LayerBaseCommitDropUpgrader.clearDroppedCommitId( Set.of( "commit-base" ), entry );

        assertThat( result ).isSameAs( entry );
    }

    private static CommitDumpEntryJson commitEntry( final String commitId, final String message )
    {
        return CommitDumpEntryJson.create()
            .commitId( commitId )
            .message( message )
            .committer( "user:system:root" )
            .timestamp( "2026-05-08T00:00:00Z" )
            .build();
    }

    private static VersionDumpEntryJson entryWithCommitId( final String commitId )
    {
        return VersionDumpEntryJson.create()
            .nodePath( "/content/my-node" )
            .nodeBlobKey( "abc" )
            .indexConfigBlobKey( "def" )
            .accessControlBlobKey( "ghi" )
            .commitId( commitId )
            .build();
    }
}
