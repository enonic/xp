package com.enonic.wem.admin.rest.resource.content.json;


import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.wem.admin.json.data.DataJson;
import com.enonic.wem.admin.json.form.FormJson;
import com.enonic.wem.api.command.content.CreateContent;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.data.ContentData;
import com.enonic.wem.api.schema.content.ContentTypeName;

public class CreateContentJson
{
    private List<AttachmentJson> attachments;

    final CreateContent createContent;

    @JsonCreator
    CreateContentJson( @JsonProperty("draft") final String draft, @JsonProperty("name") final String name,
                       @JsonProperty("displayName") final String displayName,
                       @JsonProperty("parent") final String parent,
                       @JsonProperty("contentType") final String contentType, @JsonProperty("form") final FormJson formJson,
                       @JsonProperty("contentData") final List<DataJson> dataJsonList,
                       @JsonProperty("attachments") final List<AttachmentJson> attachmentJsonList )
    {

        this.createContent = new CreateContent();
        this.createContent.draft( Boolean.valueOf( draft ) );
        this.createContent.name( name );
        this.createContent.displayName( displayName );
        this.createContent.parent( ContentPath.from( parent ) );
        this.createContent.contentType( ContentTypeName.from( contentType ) );
        this.createContent.form( formJson.getForm() );

        final ContentData contentData = new ContentData();
        for ( DataJson dataJson : dataJsonList )
        {
            contentData.add( dataJson.getData() );
        }
        this.createContent.contentData( contentData );

    }

    @JsonIgnore
    public CreateContent getCreateContent()
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
