package com.enonic.xp.core.impl.content;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;

import com.enonic.xp.content.Content;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.node.Attributes;
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

    public static final String IMPORT_ATTR = "content.import";

    public static final String UPDATE_ATTR = "content.update";

    public static final String PERMISSIONS_ATTR = "content.permissions";

    public static final String MOVE_ATTR = "content.move";

    public static final String SORT_ATTR = "content.sort";

    public static final String PATCH_ATTR = "content.patch";

    public static final String UPDATE_METADATA_ATTR = "content.updateMetadata";

    public static final String ARCHIVE_ATTR = "content.archive";

    public static final String RESTORE_ATTR = "content.restore";

    public static final String PUBLISH_ATTR = "content.publish";

    public static final String UNPUBLISH_ATTR = "content.unpublish";

    private static final Clock MILLIS_CLOCK = Clock.tick( Clock.systemUTC(), Duration.ofMillis( 1 ) );

    private static final Map<String, Function<Content, ?>> FIELD_GETTERS =
        Map.of( "displayName", Content::getDisplayName, "data", Content::getData, "x", Content::getAllExtraData, "page", Content::getPage,
                "owner", Content::getOwner, "language", Content::getLanguage, "publish", Content::getPublishInfo, "workflow",
                Content::getWorkflowInfo, "variantOf", Content::getVariantOf, "attachments", Content::getAttachments );

    public static final Set<String> EDITORIAL_FIELDS = Set.of( "displayName", "data", "x", "page", "attachments" );

    public static List<String> modifiedFields( Content existingContent, Content updatedContent )
    {
        return FIELD_GETTERS.entrySet()
            .stream()
            .filter( e -> !Objects.equals( e.getValue().apply( existingContent ), e.getValue().apply( updatedContent ) ) )
            .map( Map.Entry::getKey )
            .sorted()
            .toList();
    }

    public static Attributes versionHistoryAttr( final String key )
    {
        return Attributes.create()
            .attribute( key, GenericValue.newObject()
                .put( USER_PROPERTY, getCurrentUserKey().toString() )
                .put( OPTIME_PROPERTY, Instant.now( MILLIS_CLOCK ).toString() )
                .build() )
            .build();
    }

    public static Attributes moveVersionHistoryAttr( final List<String> modifiedFields )
    {
        return Attributes.create()
            .attribute( MOVE_ATTR, GenericValue.newObject()
                .put( FIELDS_PROPERTY, GenericValue.fromRawJava( modifiedFields ) )
                .put( USER_PROPERTY, getCurrentUserKey().toString() )
                .put( OPTIME_PROPERTY, Instant.now( MILLIS_CLOCK ).toString() )
                .build() )
            .build();
    }

    public static Attributes updateVersionHistoryAttr( final List<String> modifiedFields )
    {
        return Attributes.create()
            .attribute( UPDATE_ATTR, GenericValue.newObject()
                .put( FIELDS_PROPERTY, GenericValue.fromRawJava( modifiedFields ) )
                .put( USER_PROPERTY, getCurrentUserKey().toString() )
                .put( OPTIME_PROPERTY, Instant.now( MILLIS_CLOCK ).toString() )
                .build() )
            .attribute( VacuumConstants.PREVENT_VACUUM_ATTRIBUTE, GenericValue.stringValue( "" ) )
            .build();
    }

    public static Attributes updateMetadataHistoryAttr( final List<String> modifiedFields )
    {
        return Attributes.create()
            .attribute( UPDATE_METADATA_ATTR, GenericValue.newObject()
                .put( FIELDS_PROPERTY, GenericValue.fromRawJava( modifiedFields ) )
                .put( USER_PROPERTY, getCurrentUserKey().toString() )
                .put( OPTIME_PROPERTY, Instant.now( MILLIS_CLOCK ).toString() )
                .build() )
            .build();
    }

    public static Attributes unpublishInfoAttr( final List<String> modifiedFields )
    {
        return Attributes.create()
            .attribute( UNPUBLISH_ATTR, GenericValue.newObject()
                .put( FIELDS_PROPERTY, GenericValue.fromRawJava( modifiedFields ) )
                .put( USER_PROPERTY, getCurrentUserKey().toString() )
                .put( OPTIME_PROPERTY, Instant.now( MILLIS_CLOCK ).toString() )
                .build() )
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

    static PrincipalKey getCurrentUserKey()
    {
        final Context context = ContextAccessor.current();

        return context.getAuthInfo().getUser() != null ? context.getAuthInfo().getUser().getKey() : PrincipalKey.ofAnonymous();
    }

}
