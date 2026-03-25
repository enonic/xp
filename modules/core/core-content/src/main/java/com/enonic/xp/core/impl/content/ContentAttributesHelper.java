package com.enonic.xp.core.impl.content;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

import com.enonic.xp.content.Content;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.core.internal.Millis;
import com.enonic.xp.node.Attributes;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.VersionAttributesResolver;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.util.GenericValue;
import com.enonic.xp.vacuum.VacuumConstants;

public class ContentAttributesHelper
{
    public static final String USER_PROPERTY = "user";

    public static final String FIELDS_PROPERTY = "fields";

    public static final String OPTIME_PROPERTY = "optime";

    public static final String CREATE_ATTR = "content.create";

    public static final String DUPLICATE_ATTR = "content.duplicate";

    public static final String SYNC_ATTR = "content.sync";

    public static final String UPDATE_ATTR = "content.update";

    public static final String PERMISSIONS_ATTR = "content.permissions";

    public static final String MOVE_ATTR = "content.move";

    public static final String SORT_ATTR = "content.sort";

    public static final String PATCH_ATTR = "content.patch";

    public static final String UPDATE_METADATA_ATTR = "content.updateMetadata";

    public static final String UPDATE_WORKFLOW_ATTR = "content.updateWorkflow";

    public static final String ARCHIVE_ATTR = "content.archive";

    public static final String RESTORE_ATTR = "content.restore";

    public static final String PUBLISH_ATTR = "content.publish";

    public static final String UNPUBLISH_ATTR = "content.unpublish";

    private static final Map<String, Function<Content, ?>> FIELD_GETTERS =
        Map.ofEntries( Map.entry( "displayName", Content::getDisplayName ), Map.entry( "data", Content::getData ),
                       Map.entry( "x", Content::getMixins ), Map.entry( "page", Content::getPage ),
                       Map.entry( "owner", Content::getOwner ), Map.entry( "language", Content::getLanguage ),
                       Map.entry( "publish", Content::getPublishInfo ), Map.entry( "workflow", Content::getWorkflowInfo ),
                       Map.entry( "variantOf", Content::getVariantOf ), Map.entry( "attachments", Content::getAttachments ),
                       Map.entry( "inherit", Content::getInherit ), Map.entry( "manualOrderValue", Content::getManualOrderValue ),
                       Map.entry( "childOrder", Content::getChildOrder ), Map.entry( "parentPath", Content::getParentPath ),
                       Map.entry( "name", Content::getName ) );

    public static String[] modifiedFields( Content existingContent, Content updatedContent )
    {
        return FIELD_GETTERS.entrySet()
            .stream()
            .filter( e -> !Objects.equals( e.getValue().apply( existingContent ), e.getValue().apply( updatedContent ) ) )
            .map( Map.Entry::getKey )
            .sorted()
            .toArray( String[]::new );
    }

    public static Attributes versionHistoryAttr( final String key )
    {
        return Attributes.create()
            .attribute( key, GenericValue.newObject()
                .put( USER_PROPERTY, getCurrentUserKey().toString() )
                .put( OPTIME_PROPERTY, Millis.now().toString() )
                .build() )
            .attribute( VacuumConstants.VACUUM_SKIP_ATTRIBUTE, GenericValue.newObject().build() )
            .build();
    }

    public static Attributes layersSyncAttr( final String... modifiedFields )
    {
        return Attributes.create()
            .attribute( SYNC_ATTR, GenericValue.newObject()
                .put( FIELDS_PROPERTY, GenericValue.fromRawJava( List.of( modifiedFields ) ) )
                .put( USER_PROPERTY, getCurrentUserKey().toString() )
                .put( OPTIME_PROPERTY, Millis.now().toString() )
                .build() )
            .build();
    }

    public static Attributes versionHistoryAttr( final String key, String... modifiedFields )
    {
        return Attributes.create()
            .attribute( key, GenericValue.newObject()
                .put( FIELDS_PROPERTY, GenericValue.fromRawJava( List.of( modifiedFields ) ) )
                .put( USER_PROPERTY, getCurrentUserKey().toString() )
                .put( OPTIME_PROPERTY, Millis.now().toString() )
                .build() )
            .attribute( VacuumConstants.VACUUM_SKIP_ATTRIBUTE, GenericValue.newObject().build() )
            .build();
    }

    public static Instant getOpTime( final GenericValue attribute )
    {
        return Instant.parse( attribute.property( ContentAttributesHelper.OPTIME_PROPERTY ).asString() );
    }

    public static PrincipalKey getUser( final GenericValue attribute )
    {
        return PrincipalKey.from( attribute.property( ContentAttributesHelper.USER_PROPERTY ).asString() );
    }

    public static VersionAttributesResolver layersSyncResolver()
    {
        return ( originalNode, editedNode, branch ) -> {
            final String[] fields = resolveModifiedFields( originalNode, editedNode );
            return layersSyncAttr( fields );
        };
    }

    public static VersionAttributesResolver versionHistoryResolver( final String key )
    {
        return ( originalNode, editedNode, branch ) -> {
            final String[] fields = resolveModifiedFields( originalNode, editedNode );
            return versionHistoryAttr( key, fields );
        };
    }

    private static String[] resolveModifiedFields( final Node originalNode, final Node editedNode )
    {
        try
        {
            final Content existingContent = ContentNodeTranslator.fromNode( originalNode );
            final Content updatedContent = ContentNodeTranslator.fromNode( editedNode );
            return modifiedFields( existingContent, updatedContent );
        }
        catch ( Exception e )
        {
            return new String[0];
        }
    }

    static PrincipalKey getCurrentUserKey()
    {
        final Context context = ContextAccessor.current();
        return context.getAuthInfo().getUser() != null ? context.getAuthInfo().getUser().getKey() : PrincipalKey.ofAnonymous();
    }
}
