package com.enonic.xp.repo.impl.dump.upgrade.model8to9;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.jspecify.annotations.Nullable;

import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.ContentPropertyNames;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.repo.impl.NodeStoreVersion;
import com.enonic.xp.repo.impl.dump.serializer.json.VersionDumpEntryJson;
import com.enonic.xp.repo.impl.dump.upgrade.BranchEntryUpgrader;
import com.enonic.xp.vacuum.VacuumConstants;

/**
 * Stamps version history attributes on every version of every content-typed
 * node, mirroring what today's Content commands write via
 * {@code ContentAttributesHelper}: a {@code content.<op>} attribute carrying
 * {@code user} and {@code optime}, plus the {@code vacuum.skip} marker so the
 * new vacuum process leaves migrated history alone.
 * <p>
 * A {@code content.<op>} attribute is only stamped when the version's commit
 * message starts with a known content commit prefix
 * ({@link ContentConstants#PUBLISH_COMMIT_PREFIX}, {@link ContentConstants#UNPUBLISH_COMMIT_PREFIX},
 * {@link ContentConstants#RESTORE_COMMIT_PREFIX}, {@link ContentConstants#ARCHIVE_COMMIT_PREFIX}).
 * Versions without a known content commit get only the {@code vacuum.skip} marker.
 * <p>
 * Project-repo scoping is the caller's responsibility: {@link DumpUpgrader8to9}
 * only invokes this upgrader when the current repository's id starts with
 * {@code com.enonic.cms.}.
 */
public final class VersionHistoryMigrationUpgrader
    implements BranchEntryUpgrader
{
    public record CommitInfo(@Nullable String message, @Nullable String committer, @Nullable String timestamp)
    {
    }

    public record ContentHistoryContext(@Nullable String draftVersionId, @Nullable String masterVersionId)
    {
    }

    static final String CONTENT_PUBLISH_ATTR = "content.publish";

    static final String CONTENT_UNPUBLISH_ATTR = "content.unpublish";

    static final String CONTENT_RESTORE_ATTR = "content.restore";

    static final String CONTENT_ARCHIVE_ATTR = "content.archive";

    static final String CONTENT_UPDATE_ATTR = "content.update";

    private static final String USER_PROPERTY = "user";

    private static final String OPTIME_PROPERTY = "optime";

    public static VersionDumpEntryJson stampVersion( final NodeStoreVersion nodeVersion, final VersionDumpEntryJson entry,
                                                     @Nullable final CommitInfo commitInfo )
    {
        return stampVersion( nodeVersion, entry, commitInfo, null );
    }

    public static VersionDumpEntryJson stampVersion( final NodeStoreVersion nodeVersion, final VersionDumpEntryJson entry,
                                                     @Nullable final CommitInfo commitInfo, @Nullable final ContentHistoryContext ctx )
    {
        if ( !ContentConstants.CONTENT_NODE_COLLECTION.equals( nodeVersion.nodeType() ) )
        {
            return entry;
        }
        final Map<String, Object> attributes = new LinkedHashMap<>();
        if ( isKnownContentCommit( commitInfo ) )
        {
            attributes.put( resolveAttrKey( commitInfo ), resolveAttrValue( entry, commitInfo ) );
        }
        else if ( isDraftNotMaster( entry, ctx ) )
        {
            attributes.put( CONTENT_UPDATE_ATTR, resolveDraftUpdateAttrValue( nodeVersion, entry ) );
        }
        attributes.put( VacuumConstants.VACUUM_SKIP_ATTRIBUTE, Map.of() );
        return VersionDumpEntryJson.create( entry ).attributes( attributes ).build();
    }

    public static ContentHistoryContext buildContext( final Map<String, List<String>> activationsForNode )
    {
        final String draftVersionId = resolveVersionForBranch( activationsForNode, ContentConstants.BRANCH_DRAFT.getValue() );
        final String masterVersionId = resolveVersionForBranch( activationsForNode, ContentConstants.BRANCH_MASTER.getValue() );
        return new ContentHistoryContext( draftVersionId, masterVersionId );
    }

    public static NodeStoreVersion applyPublishTime( final NodeStoreVersion nodeVersion, @Nullable final CommitInfo commitInfo )
    {
        if ( !ContentConstants.CONTENT_NODE_COLLECTION.equals( nodeVersion.nodeType() ) )
        {
            return nodeVersion;
        }
        if ( !hasPublishCommit( commitInfo ) || commitInfo.timestamp() == null )
        {
            return nodeVersion;
        }
        final PropertyTree data = nodeVersion.data() != null ? nodeVersion.data().copy() : new PropertyTree();
        final PropertySet publishSet = data.getSet( ContentPropertyNames.PUBLISH_INFO ) != null
            ? data.getSet( ContentPropertyNames.PUBLISH_INFO )
            : data.addSet( ContentPropertyNames.PUBLISH_INFO );
        publishSet.resetInstant( ContentPropertyNames.PUBLISH_TIME, Instant.parse( commitInfo.timestamp() ) );
        return NodeStoreVersion.create( nodeVersion ).data( data ).build();
    }

    private static @Nullable String resolveVersionForBranch( final Map<String, List<String>> activationsForNode, final String branch )
    {
        for ( Map.Entry<String, List<String>> entry : activationsForNode.entrySet() )
        {
            if ( entry.getValue().contains( branch ) )
            {
                return entry.getKey();
            }
        }
        return null;
    }

    private static boolean hasPublishCommit( @Nullable final CommitInfo commitInfo )
    {
        return commitInfo != null && commitInfo.message() != null &&
            commitInfo.message().startsWith( ContentConstants.PUBLISH_COMMIT_PREFIX );
    }

    private static Map<String, Object> resolveDraftUpdateAttrValue( final NodeStoreVersion nodeVersion, final VersionDumpEntryJson entry )
    {
        final Map<String, Object> attrValue = new LinkedHashMap<>();
        final String modifier = readModifier( nodeVersion );
        putIfPresent( attrValue, USER_PROPERTY, modifier );
        putIfPresent( attrValue, OPTIME_PROPERTY, entry.getTimestamp() );
        return attrValue;
    }

    private static @Nullable String readModifier( final NodeStoreVersion nodeVersion )
    {
        final PropertyTree data = nodeVersion.data();
        if ( data == null )
        {
            return null;
        }
        return data.getString( ContentPropertyNames.MODIFIER );
    }

    private static boolean isDraftNotMaster( final VersionDumpEntryJson entry, @Nullable final ContentHistoryContext ctx )
    {
        if ( ctx == null || ctx.draftVersionId() == null || entry.getVersion() == null )
        {
            return false;
        }
        return ctx.draftVersionId().equals( entry.getVersion() ) && !ctx.draftVersionId().equals( ctx.masterVersionId() );
    }


    private static Map<String, Object> resolveAttrValue( final VersionDumpEntryJson entry, final CommitInfo commitInfo )
    {
        final Map<String, Object> attrValue = new LinkedHashMap<>();
        attrValue.put( USER_PROPERTY, commitInfo.committer() );
        putIfPresent( attrValue, OPTIME_PROPERTY, commitInfo.timestamp() != null ? commitInfo.timestamp() : entry.getTimestamp() );
        return attrValue;
    }

    private static void putIfPresent( final Map<String, Object> map, final String key, @Nullable final String value )
    {
        if ( value != null )
        {
            map.put( key, value );
        }
    }


    @Override
    public VersionDumpEntryJson upgradeBranchMeta( final NodeStoreVersion nodeVersion, final VersionDumpEntryJson meta )
    {
        if ( !ContentConstants.CONTENT_NODE_COLLECTION.equals( nodeVersion.nodeType() ) )
        {
            return meta;
        }
        return VersionDumpEntryJson.create( meta )
            .attributes( Map.of( VacuumConstants.VACUUM_SKIP_ATTRIBUTE, Map.of() ) )
            .build();
    }

    private static String resolveAttrKey( final CommitInfo commitInfo )
    {
        final String message = Objects.requireNonNullElse(  commitInfo.message(), "");
        if ( message.startsWith( ContentConstants.PUBLISH_COMMIT_PREFIX ) )
        {
            return CONTENT_PUBLISH_ATTR;
        }
        if ( message.startsWith( ContentConstants.UNPUBLISH_COMMIT_PREFIX ) )
        {
            return CONTENT_UNPUBLISH_ATTR;
        }
        if ( message.startsWith( ContentConstants.RESTORE_COMMIT_PREFIX ) )
        {
            return CONTENT_RESTORE_ATTR;
        }
        return CONTENT_ARCHIVE_ATTR;
    }

    private static boolean isKnownContentCommit( @Nullable final CommitInfo commitInfo )
    {
        if ( commitInfo == null || commitInfo.message() == null )
        {
            return false;
        }
        final String message = commitInfo.message();
        return message.startsWith( ContentConstants.PUBLISH_COMMIT_PREFIX ) ||
            message.startsWith( ContentConstants.UNPUBLISH_COMMIT_PREFIX ) ||
            message.startsWith( ContentConstants.RESTORE_COMMIT_PREFIX ) ||
            message.startsWith( ContentConstants.ARCHIVE_COMMIT_PREFIX );
    }
}
