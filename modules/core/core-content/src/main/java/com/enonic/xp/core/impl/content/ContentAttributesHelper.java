package com.enonic.xp.core.impl.content;

import java.time.Instant;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.SequencedSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Function;

import org.jspecify.annotations.Nullable;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.ContentPropertyNames;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.core.internal.Millis;
import com.enonic.xp.data.PropertyTree;
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

    public static final String ORIGIN_PROPERTY = "origin";

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

    public static final String EDITORIAL_PROPERTY = "editorial";

    private static final Set<String> EDITORIAL_FIELDS =
        Set.of( ContentPropertyNames.DISPLAY_NAME, ContentPropertyNames.DATA, ContentPropertyNames.MIXINS, ContentPropertyNames.COMPONENTS,
                ContentPropertyNames.ATTACHMENT, "childOrder", "manualOrderValue" );

    private static final Map<String, Function<Node, ?>> NODE_FIELD_GETTERS =
        Map.ofEntries( Map.entry( "name", Node::name ), Map.entry( "parentPath", Node::parentPath ),
                       Map.entry( "childOrder", Node::getChildOrder ), Map.entry( "manualOrderValue", Node::getManualOrderValue ) );

    private static final String[] DATA_FIELD_NAMES =
        {ContentPropertyNames.DISPLAY_NAME, ContentPropertyNames.DATA, ContentPropertyNames.MIXINS, ContentPropertyNames.COMPONENTS,
            ContentPropertyNames.ATTACHMENT, ContentPropertyNames.OWNER, ContentPropertyNames.LANGUAGE, ContentPropertyNames.VARIANT_OF,
            ContentPropertyNames.PUBLISH_INFO, ContentPropertyNames.WORKFLOW_INFO, ContentPropertyNames.INHERIT};

    public static Instant getOpTime( final GenericValue attribute )
    {
        return Instant.parse( attribute.property( ContentAttributesHelper.OPTIME_PROPERTY ).asString() );
    }

    public static PrincipalKey getUser( final GenericValue attribute )
    {
        return PrincipalKey.from( attribute.property( ContentAttributesHelper.USER_PROPERTY ).asString() );
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

    public static VersionAttributesResolver versionHistoryResolver( final String key,
                                                                    final Map<String, VersionPropertyResolver> extraProperties )
    {
        return ( editedNode, originalNode, branch, originalAttributes ) -> {
            final SequencedSet<String> modifiedFields = resolveModifiedFields( editedNode, originalNode );
            final GenericValue.ObjectBuilder obj = GenericValue.newObject()
                .put( FIELDS_PROPERTY, GenericValue.fromRawJava( modifiedFields ) )
                .put( USER_PROPERTY, getCurrentUserKey().toString() )
                .put( OPTIME_PROPERTY, Millis.now().toString() );
            extraProperties.forEach( ( k, resolver ) -> {
                final String value = resolver.resolve( editedNode, originalNode, branch, originalAttributes );
                if ( value != null )
                {
                    obj.put( k, value );
                }
            } );
            return Attributes.create()
                .attribute( key, obj.build() )
                .attribute( VacuumConstants.VACUUM_SKIP_ATTRIBUTE, GenericValue.newObject().build() )
                .build();
        };
    }

    @FunctionalInterface
    public interface VersionPropertyResolver
    {
        @Nullable String resolve( Node editedNode, @Nullable Node originalNode, Branch branch, @Nullable Attributes originalAttributes );
    }

    public static Map.Entry<String, VersionPropertyResolver> resolveOriginProperty()
    {
        return Map.entry( ORIGIN_PROPERTY, ( _, _, branch, _ ) -> branch.getValue() );
    }

    public static Map.Entry<String, VersionPropertyResolver> resolveEditorialProperty()
    {
        return Map.entry( EDITORIAL_PROPERTY, ContentAttributesHelper::resolveEditorial );
    }

    public static Map.Entry<String, VersionPropertyResolver> resolveEditorialIfNotEditorialChange()
    {
        return Map.entry( EDITORIAL_PROPERTY, ( editedNode, originalNode, branch, originalAttributes ) -> {
            if ( originalNode == null )
            {
                return null;
            }
            final SequencedSet<String> modifiedFields = resolveModifiedFields( editedNode, originalNode );
            if ( !Collections.disjoint( modifiedFields, EDITORIAL_FIELDS ) )
            {
                return null;
            }
            return resolveEditorial( editedNode, originalNode, branch, originalAttributes );
        } );
    }

    private static SequencedSet<String> resolveModifiedFields( final Node editedNode, final Node originalNode )
    {
        final SequencedSet<String> modified = new TreeSet<>();
        if ( originalNode == null )
        {
            return modified;
        }

        for ( Map.Entry<String, Function<Node, ?>> entry : NODE_FIELD_GETTERS.entrySet() )
        {
            if ( !Objects.equals( entry.getValue().apply( originalNode ), entry.getValue().apply( editedNode ) ) )
            {
                modified.add( entry.getKey() );
            }
        }

        final PropertyTree originalData = originalNode.data();
        final PropertyTree editedData = editedNode.data();

        for ( final String fieldName : DATA_FIELD_NAMES )
        {
            if ( !Objects.equals( originalData.getProperties( fieldName ), editedData.getProperties( fieldName ) ) )
            {
                modified.add( fieldName );
            }
        }

        return modified;
    }

    private static String resolveEditorial( final Node editedNode, final Node originalNode, final Branch branch,
                                            final Attributes originalAttributes )
    {
        if ( !ContentConstants.BRANCH_DRAFT.equals( branch ) )
        {
            return null;
        }
        if ( originalAttributes != null )
        {
            for ( final var entry : originalAttributes.entrySet() )
            {
                if ( entry.getKey().startsWith( "content." ) )
                {
                    final GenericValue editorial = entry.getValue().optional( EDITORIAL_PROPERTY ).orElse( null );
                    if ( editorial != null )
                    {
                        return editorial.asString();
                    }
                }
            }
        }
        if ( originalNode != null )
        {
            return originalNode.getNodeVersionId().toString();
        }
        return null;
    }

    static PrincipalKey getCurrentUserKey()
    {
        final Context context = ContextAccessor.current();
        return context.getAuthInfo().getUser() != null ? context.getAuthInfo().getUser().getKey() : PrincipalKey.ofAnonymous();
    }
}
