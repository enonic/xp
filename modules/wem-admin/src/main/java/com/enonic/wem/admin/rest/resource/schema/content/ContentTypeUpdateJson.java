package com.enonic.wem.admin.rest.resource.schema.content;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.wem.admin.json.icon.IconJson;
import com.enonic.wem.api.command.schema.content.UpdateContentType;
import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.schema.content.editor.ContentTypeEditor;
import com.enonic.wem.core.schema.content.serializer.ContentTypeXmlSerializer;

import static com.enonic.wem.api.command.Commands.contentType;
import static com.enonic.wem.api.schema.content.ContentType.newContentType;

public class ContentTypeUpdateJson
{

    private final UpdateContentType updateContentType;

    private final ContentTypeName name;

    @JsonCreator
    public ContentTypeUpdateJson( @JsonProperty("contentTypeToUpdate") final String contentTypeToUpdateAsString,
                                  @JsonProperty("name") final String nameAsString, @JsonProperty("config") final String config,
                                  @JsonProperty("icon") final IconJson iconJson )
    {

        final ContentTypeName contentTypeToUpdate = ContentTypeName.from( contentTypeToUpdateAsString );
        name = ContentTypeName.from( nameAsString );

        final ContentType parsed = parse( name, config, iconJson );

        final ContentTypeEditor editor = new ContentTypeEditor()
        {
            @Override
            public ContentType edit( final ContentType toEdit )
            {
                final ContentType.Builder builder = ContentType.newContentType( toEdit ).
                    name( parsed.getName() ).
                    displayName( parsed.getDisplayName() ).
                    superType( parsed.getSuperType() ).
                    setAbstract( parsed.isAbstract() ).
                    setFinal( parsed.isFinal() ).
                    contentDisplayNameScript( parsed.getContentDisplayNameScript() ).
                    form( parsed.form() );

                if ( iconJson != null )
                {
                    builder.icon( parsed.getIcon() );
                }

                return builder.build();
            }
        };

        updateContentType = contentType().update().
            contentTypeName( contentTypeToUpdate ).
            editor( editor );

    }

    private ContentType parse( final ContentTypeName name, final String config, final IconJson iconJson )
    {
        ContentType contentType = new ContentTypeXmlSerializer().overrideName( name.toString() ).toContentType( config );
        if ( iconJson != null )
        {
            contentType = newContentType( contentType ).icon( iconJson.getIcon() ).build();
        }
        return contentType;
    }

    @JsonIgnore
    public UpdateContentType getUpdateContentType()
    {
        return updateContentType;
    }

    @JsonIgnore
    public ContentTypeName getName()
    {
        return name;
    }
}
