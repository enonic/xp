package com.enonic.xp.repo.impl.dump.upgrade.model8to9;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import org.jspecify.annotations.Nullable;

import com.enonic.xp.content.ContentConstants;
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

    static final String CONTENT_PUBLISH_ATTR = "content.publish";

    static final String CONTENT_UNPUBLISH_ATTR = "content.unpublish";

    static final String CONTENT_RESTORE_ATTR = "content.restore";

    static final String CONTENT_ARCHIVE_ATTR = "content.archive";

    private static final String USER_PROPERTY = "user";

    private static final String OPTIME_PROPERTY = "optime";

    public static VersionDumpEntryJson stampVersion( final NodeStoreVersion nodeVersion, final VersionDumpEntryJson entry,
                                                     @Nullable final CommitInfo commitInfo )
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
        attributes.put( VacuumConstants.VACUUM_SKIP_ATTRIBUTE, Map.of() );
        return VersionDumpEntryJson.create( entry ).attributes( attributes ).build();
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
