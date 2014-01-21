package com.enonic.wem.admin.rest.resource.schema.content;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.wem.admin.json.icon.IconJson;
import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.core.schema.content.serializer.ContentTypeXmlSerializer;

import static com.enonic.wem.api.schema.content.ContentType.newContentType;

public class ContentTypeUpdateJson
{

    private final ContentTypeName contentTypeToUpdate;

    private final ContentTypeName name;

    private final ContentType contentTypeUpdate;

    private final IconJson iconJson;

    @JsonCreator
    public ContentTypeUpdateJson( @JsonProperty("contentTypeToUpdate") final String contentTypeToUpdateAsString,
                                  @JsonProperty("name") final String nameAsString, @JsonProperty("config") final String config,
                                  @JsonProperty("icon") final IconJson iconJson )
    {

        this.contentTypeToUpdate = ContentTypeName.from( contentTypeToUpdateAsString );
        this.name = ContentTypeName.from( nameAsString );
        this.iconJson = iconJson;
        this.contentTypeUpdate = parse( name, config );
    }

    private ContentType parse( final ContentTypeName name, final String config )
    {
        ContentType contentType = new ContentTypeXmlSerializer().overrideName( name.toString() ).toContentType( config );
        contentType = newContentType( contentType ).build();
        return contentType;
    }

    @JsonIgnore
    public ContentTypeName getName()
    {
        return name;
    }

    @JsonIgnore
    public ContentTypeName getContentTypeToUpdate()
    {
        return contentTypeToUpdate;
    }

    @JsonIgnore
    public ContentType getContentTypeUpdate()
    {
        return contentTypeUpdate;
    }

    @JsonIgnore
    public IconJson getIconJson()
    {
        return this.iconJson;
    }
}
