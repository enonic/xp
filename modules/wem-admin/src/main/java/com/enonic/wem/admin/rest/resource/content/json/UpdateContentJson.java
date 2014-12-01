package com.enonic.wem.admin.rest.resource.content.json;


import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.wem.admin.json.content.MetadataJson;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentName;
import com.enonic.wem.api.content.Metadata;
import com.enonic.wem.api.content.RenameContentParams;
import com.enonic.wem.api.content.UpdateContentParams;
import com.enonic.wem.api.data2.PropertyArrayJson;
import com.enonic.wem.api.data2.PropertyTree;
import com.enonic.wem.api.data2.PropertyTreeJson;
import com.enonic.wem.api.form.FormJson;
import com.enonic.wem.api.security.PrincipalKey;
import com.enonic.wem.api.security.acl.AccessControlList;

import static com.enonic.wem.api.content.Content.editContent;

public class UpdateContentJson
{
    final ContentName contentName;

    final UpdateContentParams updateContentParams;

    final RenameContentParams renameContentParams;

    @JsonCreator
    UpdateContentJson( @JsonProperty("contentId") final String contentId, @JsonProperty("contentName") final String contentName,
                       @JsonProperty("contentData") final List<PropertyArrayJson> contentDataJsonList,
                       @JsonProperty("metadata") final List<MetadataJson> metadataJsonList, @JsonProperty("form") final FormJson form,
                       @JsonProperty("displayName") final String displayName,
                       @JsonProperty("updateAttachments") final UpdateAttachmentsJson updateAttachments,
                       @JsonProperty("thumbnail") final ThumbnailJson thumbnail, @JsonProperty("draft") final String draft,
                       @JsonProperty("permissions") final List<AccessControlEntryJson> permissions,
                       @JsonProperty("inheritPermissions") final boolean inheritPermissions )
    {
        this.contentName = ContentName.from( contentName );

        final PropertyTree contentData = PropertyTreeJson.fromJson( contentDataJsonList );
        final List<Metadata> metadataList = parseMetadata( metadataJsonList );

        this.updateContentParams = new UpdateContentParams().
            contentId( ContentId.from( contentId ) ).
            modifier( PrincipalKey.ofAnonymous() ).
            updateAttachments( updateAttachments != null ? updateAttachments.getUpdateAttachments() : null ).
            editor( toBeEdited -> {
                Content.EditBuilder editContentBuilder = editContent( toBeEdited ).
                    form( form.getForm() ).
                    contentData( contentData ).
                    metadata( metadataList ).
                    draft( Boolean.valueOf( draft ) ).
                    displayName( displayName ).
                    inheritPermissions( inheritPermissions );
                if ( thumbnail != null )
                {
                    editContentBuilder = editContentBuilder.thumbnail( thumbnail.getThumbnail() );
                }
                if ( permissions != null )
                {
                    final AccessControlList acl = parseAcl( permissions );
                    editContentBuilder = editContentBuilder.accessControlList( acl );
                }
                return editContentBuilder;
            } );

        this.renameContentParams = new RenameContentParams().
            contentId( ContentId.from( contentId ) ).
            newName( this.contentName );
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
    public ContentName getContentName()
    {
        return contentName;
    }

    private List<Metadata> parseMetadata( final List<MetadataJson> metadataJsonList )
    {
        final List<Metadata> metadataList = new ArrayList<>();
        for ( MetadataJson metadataJson : metadataJsonList )
        {
            metadataList.add( metadataJson.getMetadata() );
        }
        return metadataList;
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
}
