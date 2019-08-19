package com.enonic.xp.admin.impl.rest.resource.content.json;


import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.xp.admin.impl.json.content.ExtraDataJson;
import com.enonic.xp.admin.impl.json.content.attachment.AttachmentJson;
import com.enonic.xp.content.ContentName;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.CreateContentParams;
import com.enonic.xp.content.ExtraDatas;
import com.enonic.xp.data.PropertyArrayJson;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.data.PropertyTreeJson;
import com.enonic.xp.schema.content.ContentTypeName;

public final class CreateContentJson
{
    private final CreateContentParams createContent;

    private List<AttachmentJson> attachments;

    @JsonCreator
    CreateContentJson( @JsonProperty("valid") final String valid, @JsonProperty("requireValid") final String requireValid,
                       @JsonProperty("name") final String name, @JsonProperty("displayName") final String displayName,
                       @JsonProperty("parent") final String parent, @JsonProperty("contentType") final String contentType,
                       @JsonProperty("data") final List<PropertyArrayJson> dataJsonList,
                       @JsonProperty("meta") final List<ExtraDataJson> extraDataJsonList )
    {

        final CreateContentParams.Builder paramsBuilder = CreateContentParams.create().
            requireValid( Boolean.valueOf( requireValid ) ).
            name( ContentName.from( name ) ).
            displayName( displayName ).
            parent( ContentPath.from( parent ) ).
            type( ContentTypeName.from( contentType ) );

        final PropertyTree contentData = PropertyTreeJson.fromJson( dataJsonList );
        paramsBuilder.contentData( contentData );

        final ExtraDatas.Builder extradatasBuilder = ExtraDatas.create();
        for ( ExtraDataJson extraDataJson : extraDataJsonList )
        {
            extradatasBuilder.add( extraDataJson.getExtraData() );
        }
        paramsBuilder.extraDatas( extradatasBuilder.build());
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
