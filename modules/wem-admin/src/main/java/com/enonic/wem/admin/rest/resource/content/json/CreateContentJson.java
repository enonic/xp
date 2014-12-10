package com.enonic.wem.admin.rest.resource.content.json;


import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.wem.admin.json.content.MetadataJson;
import com.enonic.wem.admin.json.content.attachment.AttachmentJson;
import com.enonic.wem.api.content.ContentName;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.CreateContentParams;
import com.enonic.wem.api.content.Metadata;
import com.enonic.wem.api.data.PropertyArrayJson;
import com.enonic.wem.api.data.PropertyTree;
import com.enonic.wem.api.data.PropertyTreeJson;
import com.enonic.wem.api.form.FormJson;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.security.acl.AccessControlList;

public class CreateContentJson
{
    private final CreateContentParams createContent;

    private List<AttachmentJson> attachments;

    @JsonCreator
    CreateContentJson( @JsonProperty("draft") final String draft, @JsonProperty("name") final String name,
                       @JsonProperty("displayName") final String displayName, @JsonProperty("parent") final String parent,
                       @JsonProperty("contentType") final String contentType, @JsonProperty("form") final FormJson formJson,
                       @JsonProperty("data") final List<PropertyArrayJson> dataJsonList,
                       @JsonProperty("attachments") final List<AttachmentJson> attachmentJsonList,
                       @JsonProperty("metadata") final List<MetadataJson> metadataJsonList,
                       @JsonProperty("permissions") final List<AccessControlEntryJson> permissions,
                       @JsonProperty("inheritPermissions") final boolean inheritPermissions )
    {

        this.createContent = new CreateContentParams();
        this.createContent.draft( Boolean.valueOf( draft ) );
        this.createContent.name( ContentName.from( name ) );
        this.createContent.displayName( displayName );
        this.createContent.parent( ContentPath.from( parent ) );
        this.createContent.contentType( ContentTypeName.from( contentType ) );
        this.createContent.form( formJson != null ? formJson.getForm() : null );

        final PropertyTree contentData = PropertyTreeJson.fromJson( dataJsonList );
        this.createContent.contentData( contentData );

        final List<Metadata> metadataList = new ArrayList<>();
        for ( MetadataJson metadataJson : metadataJsonList )
        {
            metadataList.add( metadataJson.getMetadata() );
        }
        this.createContent.metadata( metadataList );

        for ( AttachmentJson attachmentJson : attachmentJsonList )
        {
            this.createContent.attachments( attachmentJson.getAttachment() );
        }

        if ( permissions != null )
        {
            final AccessControlList acl = parseAcl( permissions );
            this.createContent.permissions( acl );
        }

        createContent.setInheritPermissions( inheritPermissions );
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

    @JsonIgnore
    public CreateContentParams getCreateContent()
    {
        return createContent;
    }

    public List<AttachmentJson> getAttachments()
    {
        return attachments;
    }

    public void setAttachments( final List<AttachmentJson> attachmentParams )
    {
        this.attachments = attachmentParams;
    }
}
