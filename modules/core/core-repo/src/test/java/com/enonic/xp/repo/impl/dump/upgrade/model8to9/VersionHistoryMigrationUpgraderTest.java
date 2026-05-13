package com.enonic.xp.repo.impl.dump.upgrade.model8to9;

import java.util.Map;

import org.junit.jupiter.api.Test;

import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.ContentPropertyNames;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeType;
import com.enonic.xp.repo.impl.NodeStoreVersion;
import com.enonic.xp.repo.impl.dump.serializer.json.VersionDumpEntryJson;
import com.enonic.xp.repo.impl.dump.upgrade.model8to9.VersionHistoryMigrationUpgrader.CommitInfo;
import com.enonic.xp.vacuum.VacuumConstants;

import static com.enonic.xp.repo.impl.dump.upgrade.model8to9.VersionHistoryMigrationUpgrader.CONTENT_ARCHIVE_ATTR;
import static com.enonic.xp.repo.impl.dump.upgrade.model8to9.VersionHistoryMigrationUpgrader.CONTENT_PUBLISH_ATTR;
import static com.enonic.xp.repo.impl.dump.upgrade.model8to9.VersionHistoryMigrationUpgrader.CONTENT_RESTORE_ATTR;
import static com.enonic.xp.repo.impl.dump.upgrade.model8to9.VersionHistoryMigrationUpgrader.CONTENT_UNPUBLISH_ATTR;
import static org.assertj.core.api.Assertions.assertThat;

class VersionHistoryMigrationUpgraderTest
{
    private static final String COMMITTER = "user:system:editor";

    private static final String COMMIT_TIMESTAMP = "2026-05-01T12:00:00Z";

    private static final String MODIFIER = "user:system:writer";

    private final VersionHistoryMigrationUpgrader upgrader = new VersionHistoryMigrationUpgrader();

    @Test
    void content_version_without_commit_gets_only_vacuum_skip()
    {
        final NodeStoreVersion nodeVersion = contentVersion( MODIFIER );

        final VersionDumpEntryJson result = VersionHistoryMigrationUpgrader.stampVersion( nodeVersion, contentEntry(), null );

        assertThat( result.getAttributes() ).containsOnlyKeys( VacuumConstants.VACUUM_SKIP_ATTRIBUTE );
    }

    @Test
    void content_version_with_unknown_commit_message_gets_only_vacuum_skip()
    {
        final VersionDumpEntryJson result = VersionHistoryMigrationUpgrader.stampVersion( contentVersion( MODIFIER ), contentEntry(),
                                                                                          new CommitInfo( "freeform note", COMMITTER, COMMIT_TIMESTAMP ) );

        assertThat( result.getAttributes() ).containsOnlyKeys( VacuumConstants.VACUUM_SKIP_ATTRIBUTE );
    }

    @Test
    void content_version_with_publish_commit_uses_commit_user_and_commit_timestamp()
    {
        final VersionDumpEntryJson result =
            VersionHistoryMigrationUpgrader.stampVersion( contentVersion( MODIFIER ), contentEntry(),
                                                          new CommitInfo( ContentConstants.PUBLISH_COMMIT_PREFIX, COMMITTER, COMMIT_TIMESTAMP ) );

        assertThat( result.getAttributes() ).containsEntry( CONTENT_PUBLISH_ATTR,
                                                            Map.of( "user", COMMITTER, "optime", COMMIT_TIMESTAMP ) );
        assertThat( result.getAttributes() ).containsEntry( VacuumConstants.VACUUM_SKIP_ATTRIBUTE, Map.of() );
    }

    @Test
    void content_version_with_unpublish_commit_uses_commit_user_and_commit_timestamp()
    {
        final VersionDumpEntryJson result = VersionHistoryMigrationUpgrader.stampVersion( contentVersion( MODIFIER ), contentEntry(),
                                                                                          new CommitInfo(
                                                                                              ContentConstants.UNPUBLISH_COMMIT_PREFIX,
                                                                                              COMMITTER, COMMIT_TIMESTAMP ) );

        assertThat( result.getAttributes() ).containsEntry( CONTENT_UNPUBLISH_ATTR,
                                                            Map.of( "user", COMMITTER, "optime", COMMIT_TIMESTAMP ) );
    }

    @Test
    void content_version_with_archive_commit_uses_commit_user_and_commit_timestamp()
    {
        final VersionDumpEntryJson result =
            VersionHistoryMigrationUpgrader.stampVersion( contentVersion( MODIFIER ), contentEntry(),
                                                          new CommitInfo( ContentConstants.ARCHIVE_COMMIT_PREFIX, COMMITTER, COMMIT_TIMESTAMP ) );

        assertThat( result.getAttributes() ).containsEntry( CONTENT_ARCHIVE_ATTR,
                                                            Map.of( "user", COMMITTER, "optime", COMMIT_TIMESTAMP ) );
    }

    @Test
    void content_version_with_restore_commit_uses_commit_user_and_commit_timestamp()
    {
        final VersionDumpEntryJson result =
            VersionHistoryMigrationUpgrader.stampVersion( contentVersion( MODIFIER ), contentEntry(),
                                                          new CommitInfo( ContentConstants.RESTORE_COMMIT_PREFIX, COMMITTER, COMMIT_TIMESTAMP ) );

        assertThat( result.getAttributes() ).containsEntry( CONTENT_RESTORE_ATTR,
                                                            Map.of( "user", COMMITTER, "optime", COMMIT_TIMESTAMP ) );
    }

    @Test
    void known_commit_without_timestamp_falls_back_to_version_timestamp()
    {
        final VersionDumpEntryJson result = VersionHistoryMigrationUpgrader.stampVersion( contentVersion( MODIFIER ), contentEntry(),
                                                                                          new CommitInfo(
                                                                                              ContentConstants.PUBLISH_COMMIT_PREFIX,
                                                                                              COMMITTER, null ) );

        assertThat( result.getAttributes() ).containsEntry( CONTENT_PUBLISH_ATTR,
                                                            Map.of( "user", COMMITTER, "optime", "2026-04-01T00:00:00Z" ) );
    }

    @Test
    void non_content_version_is_unchanged()
    {
        final NodeStoreVersion nodeVersion = NodeStoreVersion.create()
            .id( NodeId.from( "node-1" ) )
            .nodeType( NodeType.from( "media" ) )
            .build();
        final VersionDumpEntryJson entry = contentEntry();

        final VersionDumpEntryJson result = VersionHistoryMigrationUpgrader.stampVersion( nodeVersion, entry, null );

        assertThat( result ).isSameAs( entry );
    }

    @Test
    void branchMeta_for_content_node_gets_only_vacuum_skip()
    {
        final NodeStoreVersion nodeVersion = contentVersion( MODIFIER );

        final VersionDumpEntryJson result = upgrader.upgradeBranchMeta( nodeVersion, contentEntry() );

        assertThat( result.getAttributes() ).containsOnlyKeys( VacuumConstants.VACUUM_SKIP_ATTRIBUTE );
        assertThat( result.getNodePath() ).isEqualTo( "/content/my-node" );
    }

    @Test
    void branchMeta_for_non_content_node_is_unchanged()
    {
        final NodeStoreVersion nodeVersion = NodeStoreVersion.create()
            .id( NodeId.from( "node-1" ) )
            .nodeType( NodeType.from( "media" ) )
            .build();
        final VersionDumpEntryJson meta = contentEntry();

        final VersionDumpEntryJson result = upgrader.upgradeBranchMeta( nodeVersion, meta );

        assertThat( result ).isSameAs( meta );
    }

    private static NodeStoreVersion contentVersion( final String modifier )
    {
        final PropertyTree data = new PropertyTree();
        if ( modifier != null )
        {
            data.setString( ContentPropertyNames.MODIFIER, modifier );
        }
        return NodeStoreVersion.create()
            .id( NodeId.from( "node-1" ) )
            .nodeType( ContentConstants.CONTENT_NODE_COLLECTION )
            .data( data )
            .build();
    }

    private static VersionDumpEntryJson contentEntry()
    {
        return VersionDumpEntryJson.create()
            .nodePath( "/content/my-node" )
            .nodeBlobKey( "abc" )
            .indexConfigBlobKey( "def" )
            .accessControlBlobKey( "ghi" )
            .timestamp( "2026-04-01T00:00:00Z" )
            .build();
    }
}
