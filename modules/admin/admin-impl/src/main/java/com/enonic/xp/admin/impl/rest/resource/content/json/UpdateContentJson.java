package com.enonic.xp.admin.impl.rest.resource.content.json;


import java.time.Instant;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.xp.admin.impl.json.content.ExtraDataJson;
import com.enonic.xp.admin.impl.json.content.ContentWorkflowInfoJson;
import com.enonic.xp.content.ApplyContentPermissionsParams;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentName;
import com.enonic.xp.content.ContentPublishInfo;
import com.enonic.xp.content.ExtraDatas;
import com.enonic.xp.content.RenameContentParams;
import com.enonic.xp.content.UpdateContentParams;
import com.enonic.xp.content.WorkflowInfo;
import com.enonic.xp.data.PropertyArrayJson;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.data.PropertyTreeJson;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.acl.AccessControlList;

public final class UpdateContentJson
{
    final ContentName contentName;

    final Instant publishFromInstant;
    
    final Instant publishToInstant;

    final UpdateContentParams updateContentParams;

    final RenameContentParams renameContentParams;

    final ApplyContentPermissionsParams applyContentPermissionsParams;

    @JsonCreator
    UpdateContentJson( @JsonProperty("contentId") final String contentId, @JsonProperty("contentName") final String contentName,
                       @JsonProperty("data") final List<PropertyArrayJson> propertyArrayJsonList,
                       @JsonProperty("meta") final List<ExtraDataJson> extraDataJsonList,
                       @JsonProperty("displayName") final String displayName, @JsonProperty("requireValid") final String requireValid,
                       @JsonProperty("owner") final String owner, @JsonProperty("language") final String language,
                       @JsonProperty("publishFrom") final String publishFrom, @JsonProperty("publishTo") final String publishTo,
                       @JsonProperty("permissions") final List<AccessControlEntryJson> permissions,
                       @JsonProperty("inheritPermissions") final boolean inheritPermissions,
                       @JsonProperty("overwriteChildPermissions") final boolean overwriteChildPermissions,
                       @JsonProperty("workflow") final ContentWorkflowInfoJson workflowInfo)
    {
        this.contentName = ContentName.from( contentName );
        this.publishFromInstant = StringUtils.isNotEmpty( publishFrom ) ? Instant.parse( publishFrom ) : null;
        this.publishToInstant = StringUtils.isNotEmpty( publishTo ) ? Instant.parse( publishTo ) : null;

        final PropertyTree contentData = PropertyTreeJson.fromJson( propertyArrayJsonList );
        final ExtraDatas extraDatas = parseExtradata( extraDataJsonList );

        this.updateContentParams = new UpdateContentParams().
            requireValid( Boolean.valueOf( requireValid ) ).
            contentId( ContentId.from( contentId ) ).
            modifier( PrincipalKey.ofAnonymous() ).
            editor( edit -> {
                edit.data = contentData;
                edit.extraDatas = extraDatas;
                edit.displayName = displayName;
                edit.owner = StringUtils.isNotEmpty( owner ) ? PrincipalKey.from( owner ) : null;
                edit.language = StringUtils.isNotEmpty( language ) ? Locale.forLanguageTag( language ) : null;
                                
                edit.publishInfo = ContentPublishInfo.create().
                    first( edit.publishInfo == null ? null : edit.publishInfo.getFirst() ).
                    from( publishFromInstant ).
                    to( publishToInstant ).
                    build();
                edit.language = StringUtils.isNotEmpty( language ) ? Locale.forLanguageTag( language ) : null;
                edit.inheritPermissions = inheritPermissions;
                edit.permissions = parseAcl( permissions );
                edit.workflowInfo = parseWorkflowInfo(workflowInfo);
            } );

        this.renameContentParams = RenameContentParams.create().
            contentId( ContentId.from( contentId ) ).
            newName( this.contentName ).
            build();

        this.applyContentPermissionsParams = ApplyContentPermissionsParams.create().
            contentId( ContentId.from( contentId ) ).
            overwriteChildPermissions( overwriteChildPermissions ).
            build();
    }

    @JsonIgnore
    public UpdateContentParams getUpdateContentParams()
    {
        return updateContentParams;
    }

    @JsonIgnore
    public RenameContentParams getRenameContentParams()
    {
        return renameContentParams;
    }

    @JsonIgnore
    public ApplyContentPermissionsParams getApplyContentPermissionsParams()
    {
        return applyContentPermissionsParams;
    }


    @JsonIgnore
    public ContentName getContentName()
    {
        return contentName;
    }

    @JsonIgnore
    public Instant getPublishFromInstant()
    {
        return publishFromInstant;
    }

    @JsonIgnore
    public Instant getPublishToInstant()
    {
        return publishToInstant;
    }

    private ExtraDatas parseExtradata( final List<ExtraDataJson> extraDataJsonList )
    {
        final ExtraDatas.Builder extradatasBuilder = ExtraDatas.create();
        for ( ExtraDataJson extraDataJson : extraDataJsonList )
        {
            extradatasBuilder.add( extraDataJson.getExtraData() );
        }
        return extradatasBuilder.build();
    }

    private AccessControlList parseAcl( final List<AccessControlEntryJson> accessControlListJson )
    {
        final AccessControlList.Builder builder = AccessControlList.create();
        for ( final AccessControlEntryJson entryJson : accessControlListJson )
        {
            builder.add( entryJson.getSourceEntry() );
        }
        return builder.build();
    }

    private WorkflowInfo parseWorkflowInfo( final ContentWorkflowInfoJson contentWorkflowInfoJson )
    {
        if(contentWorkflowInfoJson == null) {
            return null;
        }

        return WorkflowInfo.create().
            state( contentWorkflowInfoJson.getState() ).
            checks( contentWorkflowInfoJson.getChecks() ).
            build();
    }
}
