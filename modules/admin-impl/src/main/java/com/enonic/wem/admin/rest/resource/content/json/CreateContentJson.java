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
import com.enonic.wem.api.content.Metadatas;
import com.enonic.wem.api.data.PropertyArrayJson;
import com.enonic.wem.api.data.PropertyTree;
import com.enonic.wem.api.data.PropertyTreeJson;
import com.enonic.wem.api.schema.content.ContentTypeName;

public final class CreateContentJson
{
    private final CreateContentParams createContent;

    private List<AttachmentJson> attachments;

    @JsonCreator
    CreateContentJson( @JsonProperty("valid") final String valid, @JsonProperty("requireValid") final String requireValid,
                       @JsonProperty("name") final String name, @JsonProperty("displayName") final String displayName,
                       @JsonProperty("parent") final String parent, @JsonProperty("contentType") final String contentType,
                       @JsonProperty("data") final List<PropertyArrayJson> dataJsonList,
                       @JsonProperty("meta") final List<MetadataJson> metadataJsonList )
    {

        final CreateContentParams.Builder paramsBuilder = CreateContentParams.create().
            requireValid( Boolean.valueOf( requireValid ) ).
            name( ContentName.from( name ) ).
            displayName( displayName ).
            parent( ContentPath.from( parent ) ).
            type( ContentTypeName.from( contentType ) );

        final PropertyTree contentData = PropertyTreeJson.fromJson( dataJsonList );
        paramsBuilder.contentData( contentData );

        final Metadatas.Builder metadatasBuilder = Metadatas.builder();
        for ( MetadataJson metadataJson : metadataJsonList )
        {
            metadatasBuilder.add( metadataJson.getMetadata() );
        }
        paramsBuilder.metadata( metadatasBuilder.build());
        paramsBuilder.inheritPermissions( true );

        this.createContent = paramsBuilder.build();
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
