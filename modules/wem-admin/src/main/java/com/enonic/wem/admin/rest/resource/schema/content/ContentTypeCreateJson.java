package com.enonic.wem.admin.rest.resource.schema.content;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.wem.admin.json.icon.IconJson;
import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.schema.content.CreateContentTypeParams;
import com.enonic.wem.api.xml.XmlSerializers;

public class ContentTypeCreateJson
{
    private final CreateContentTypeParams createContentType;

    private final IconJson iconJson;

    @JsonCreator
    public ContentTypeCreateJson( @JsonProperty("name") final String nameAsString, @JsonProperty("config") final String config,
                                  @JsonProperty("icon") final IconJson iconJson )
    {

        final ContentTypeName name = ContentTypeName.from( nameAsString );

        final ContentType.Builder builder = ContentType.newContentType().name( name );
        XmlSerializers.contentType().parse( config ).to( builder );
        final ContentType contentType = builder.build();

        this.iconJson = iconJson;

        createContentType = new CreateContentTypeParams().
            name( name ).
            displayName( contentType.getDisplayName() ).
            description( contentType.getDescription() ).
            superType( contentType.getSuperType() ).
            setAbstract( contentType.isAbstract() ).
            setFinal( contentType.isFinal() ).
            form( contentType.form() ).
            contentDisplayNameScript( contentType.getContentDisplayNameScript() );
    }

    @JsonIgnore
    public CreateContentTypeParams getCreateContentType()
    {
        return createContentType;
    }

    @JsonIgnore
    public IconJson getIconJson()
    {
        return this.iconJson;
    }

}
