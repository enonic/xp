package com.enonic.xp.repo.impl.dump.upgrade.model8to9;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.ContentPropertyNames;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeType;
import com.enonic.xp.repo.impl.NodeStoreVersion;
import com.enonic.xp.repo.impl.dump.serializer.json.VersionDumpEntryJson;
import com.enonic.xp.repo.impl.dump.upgrade.model8to9.VersionHistoryMigrationUpgrader.CommitInfo;
import com.enonic.xp.repo.impl.dump.upgrade.model8to9.VersionHistoryMigrationUpgrader.ContentHistoryContext;
import com.enonic.xp.vacuum.VacuumConstants;

import static com.enonic.xp.repo.impl.dump.upgrade.model8to9.VersionHistoryMigrationUpgrader.CONTENT_ARCHIVE_ATTR;
import static com.enonic.xp.repo.impl.dump.upgrade.model8to9.VersionHistoryMigrationUpgrader.CONTENT_PUBLISH_ATTR;
import static com.enonic.xp.repo.impl.dump.upgrade.model8to9.VersionHistoryMigrationUpgrader.CONTENT_RESTORE_ATTR;
import static com.enonic.xp.repo.impl.dump.upgrade.model8to9.VersionHistoryMigrationUpgrader.CONTENT_UNPUBLISH_ATTR;
import static com.enonic.xp.repo.impl.dump.upgrade.model8to9.VersionHistoryMigrationUpgrader.CONTENT_UPDATE_ATTR;
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

    @Test
    void stampVersion_draft_not_master_without_known_commit_emits_content_update()
    {
        final VersionDumpEntryJson entry = VersionDumpEntryJson.create( contentEntry() ).version( "v-draft" ).build();
        final ContentHistoryContext ctx = new ContentHistoryContext( "v-draft", "v-master" );

        final VersionDumpEntryJson result = VersionHistoryMigrationUpgrader.stampVersion( contentVersion( MODIFIER ), entry, null, ctx );

        assertThat( result.getAttributes() ).containsEntry( CONTENT_UPDATE_ATTR,
                                                            Map.of( "user", MODIFIER, "optime", "2026-04-01T00:00:00Z" ) );
        assertThat( result.getAttributes() ).containsEntry( VacuumConstants.VACUUM_SKIP_ATTRIBUTE, Map.of() );
    }

    @Test
    void stampVersion_draft_not_master_without_modifier_omits_user_in_content_update()
    {
        final VersionDumpEntryJson entry = VersionDumpEntryJson.create( contentEntry() ).version( "v-draft" ).build();
        final ContentHistoryContext ctx = new ContentHistoryContext( "v-draft", "v-master" );

        final VersionDumpEntryJson result = VersionHistoryMigrationUpgrader.stampVersion( contentVersion( null ), entry, null, ctx );

        assertThat( result.getAttributes() ).containsEntry( CONTENT_UPDATE_ATTR, Map.of( "optime", "2026-04-01T00:00:00Z" ) );
    }

    @Test
    void stampVersion_draft_equals_master_without_known_commit_does_not_emit_content_update()
    {
        final VersionDumpEntryJson entry = VersionDumpEntryJson.create( contentEntry() ).version( "v-same" ).build();
        final ContentHistoryContext ctx = new ContentHistoryContext( "v-same", "v-same" );

        final VersionDumpEntryJson result = VersionHistoryMigrationUpgrader.stampVersion( contentVersion( MODIFIER ), entry, null, ctx );

        assertThat( result.getAttributes() ).containsOnlyKeys( VacuumConstants.VACUUM_SKIP_ATTRIBUTE );
    }

    @Test
    void stampVersion_draft_with_known_commit_uses_existing_content_publish_path()
    {
        final VersionDumpEntryJson entry = VersionDumpEntryJson.create( contentEntry() ).version( "v-draft" ).build();
        final ContentHistoryContext ctx = new ContentHistoryContext( "v-draft", "v-master" );

        final VersionDumpEntryJson result = VersionHistoryMigrationUpgrader.stampVersion( contentVersion( MODIFIER ), entry,
                                                                                          new CommitInfo(
                                                                                              ContentConstants.PUBLISH_COMMIT_PREFIX,
                                                                                              COMMITTER, COMMIT_TIMESTAMP ),
                                                                                          ctx );

        assertThat( result.getAttributes() ).containsEntry( CONTENT_PUBLISH_ATTR,
                                                            Map.of( "user", COMMITTER, "optime", COMMIT_TIMESTAMP ) );
        assertThat( result.getAttributes() ).doesNotContainKey( CONTENT_UPDATE_ATTR );
    }

    @Test
    void buildContext_resolves_draft_and_master_version_ids()
    {
        final Map<String, List<String>> activations =
            Map.of( "v-draft", List.of( "draft" ), "v-master", List.of( "master" ) );

        final ContentHistoryContext ctx = VersionHistoryMigrationUpgrader.buildContext( activations );

        assertThat( ctx.draftVersionId() ).isEqualTo( "v-draft" );
        assertThat( ctx.masterVersionId() ).isEqualTo( "v-master" );
    }

    @Test
    void buildContext_resolves_null_when_branch_absent()
    {
        final ContentHistoryContext ctx =
            VersionHistoryMigrationUpgrader.buildContext( Map.of( "v-draft", List.of( "draft" ) ) );

        assertThat( ctx.draftVersionId() ).isEqualTo( "v-draft" );
        assertThat( ctx.masterVersionId() ).isNull();
    }

    @Test
    void applyPublishTime_publish_commit_adds_publish_time_when_publish_set_absent()
    {
        final NodeStoreVersion nodeVersion = contentVersion( MODIFIER );
        final CommitInfo commit = new CommitInfo( ContentConstants.PUBLISH_COMMIT_PREFIX, COMMITTER, "2026-04-15T00:00:00Z" );

        final NodeStoreVersion result = VersionHistoryMigrationUpgrader.applyPublishTime( nodeVersion, commit );

        assertThat( result.data().getInstant( "publish.time" ) ).isEqualTo( "2026-04-15T00:00:00Z" );
    }

    @Test
    void applyPublishTime_publish_commit_adds_publish_time_to_existing_publish_set()
    {
        final NodeStoreVersion nodeVersion = contentVersionWithPublishFrom( MODIFIER, "2026-01-01T00:00:00Z" );
        final CommitInfo commit = new CommitInfo( ContentConstants.PUBLISH_COMMIT_PREFIX, COMMITTER, "2026-04-15T00:00:00Z" );

        final NodeStoreVersion result = VersionHistoryMigrationUpgrader.applyPublishTime( nodeVersion, commit );

        assertThat( result.data().getInstant( "publish.time" ) ).isEqualTo( "2026-04-15T00:00:00Z" );
        assertThat( result.data().getInstant( "publish.from" ) ).isEqualTo( "2026-01-01T00:00:00Z" );
    }

    @Test
    void applyPublishTime_non_publish_commit_is_unchanged()
    {
        final NodeStoreVersion nodeVersion = contentVersion( MODIFIER );
        final CommitInfo commit = new CommitInfo( ContentConstants.UNPUBLISH_COMMIT_PREFIX, COMMITTER, "2026-04-15T00:00:00Z" );

        final NodeStoreVersion result = VersionHistoryMigrationUpgrader.applyPublishTime( nodeVersion, commit );

        assertThat( result ).isSameAs( nodeVersion );
    }

    @Test
    void applyPublishTime_non_content_node_is_unchanged()
    {
        final NodeStoreVersion nodeVersion = NodeStoreVersion.create()
            .id( NodeId.from( "node-1" ) )
            .nodeType( NodeType.from( "media" ) )
            .data( new PropertyTree() )
            .build();

        final NodeStoreVersion result = VersionHistoryMigrationUpgrader.applyPublishTime( nodeVersion,
                                                                                          new CommitInfo(
                                                                                              ContentConstants.PUBLISH_COMMIT_PREFIX,
                                                                                              COMMITTER, "2026-04-15T00:00:00Z" ) );

        assertThat( result ).isSameAs( nodeVersion );
    }

    @Test
    void applyPublishTime_null_commit_is_unchanged()
    {
        final NodeStoreVersion nodeVersion = contentVersion( MODIFIER );

        final NodeStoreVersion result = VersionHistoryMigrationUpgrader.applyPublishTime( nodeVersion, null );

        assertThat( result ).isSameAs( nodeVersion );
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

    private static NodeStoreVersion contentVersionWithPublishFrom( final String modifier, final String publishFrom )
    {
        final PropertyTree data = new PropertyTree();
        if ( modifier != null )
        {
            data.setString( ContentPropertyNames.MODIFIER, modifier );
        }
        final PropertySet publishSet = data.addSet( ContentPropertyNames.PUBLISH_INFO );
        publishSet.setInstant( ContentPropertyNames.PUBLISH_FROM, java.time.Instant.parse( publishFrom ) );
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
