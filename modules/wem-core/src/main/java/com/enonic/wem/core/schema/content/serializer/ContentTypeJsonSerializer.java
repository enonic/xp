package com.enonic.wem.core.schema.content.serializer;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import com.enonic.wem.api.form.FormItem;
import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.support.serializer.AbstractJsonSerializer;
import com.enonic.wem.api.support.serializer.JsonParsingException;
import com.enonic.wem.api.support.serializer.JsonSerializerUtil;

import static com.enonic.wem.api.schema.content.ContentType.newContentType;

public class ContentTypeJsonSerializer
    extends AbstractJsonSerializer<ContentType>
    implements ContentTypeSerializer
{

    private boolean includeCreatedTime = false;

    private boolean includeModifiedTime = false;

    private FormItemsJsonSerializer formItemsSerializer = new FormItemsJsonSerializer( objectMapper() );

    public ContentTypeJsonSerializer includeCreatedTime( final boolean value )
    {
        includeCreatedTime = value;
        return this;
    }

    public ContentTypeJsonSerializer includeModifiedTime( final boolean value )
    {
        includeModifiedTime = value;
        return this;
    }

    @Override
    protected JsonNode serialize( final ContentType contentType )
    {
        final ObjectMapper mapper = new ObjectMapper();
        final ObjectNode objectNode = mapper.createObjectNode();
        objectNode.put( "name", contentType.getName().toString() );
        objectNode.put( "displayName", contentType.getDisplayName() );
        objectNode.put( "contentDisplayNameScript", contentType.getContentDisplayNameScript() );
        objectNode.put( "superType", contentType.getSuperType() != null ? contentType.getSuperType().toString() : null );
        objectNode.put( "isAbstract", contentType.isAbstract() );
        objectNode.put( "isFinal", contentType.isFinal() );
        objectNode.put( "isBuiltIn", contentType.isBuiltIn() );
        objectNode.put( "allowChildContent", contentType.allowChildContent() );

        if ( includeCreatedTime )
        {
            JsonSerializerUtil.setDateTimeValue( "createdTime", contentType.getCreatedTime(), objectNode );
        }
        if ( includeModifiedTime )
        {
            JsonSerializerUtil.setDateTimeValue( "modifiedTime", contentType.getModifiedTime(), objectNode );
        }
        if ( contentType.form() == null )
        {
            objectNode.putNull( "form" );
        }
        else
        {
            objectNode.put( "form", formItemsSerializer.serialize( contentType.form() ) );
        }
        return objectNode;
    }

    @Override
    public ContentType toContentType( String json )
        throws JsonParsingException
    {
        return toObject( json );
    }

    @Override
    protected ContentType parse( final JsonNode contentTypeNode )
    {
        final String superTypeValue = JsonSerializerUtil.getStringValue( "superType", contentTypeNode );
        final ContentTypeName superType = superTypeValue != null ? ContentTypeName.from( superTypeValue ) : null;

        final ContentType.Builder builder = newContentType();
        builder.name( JsonSerializerUtil.getStringValue( "name", contentTypeNode ) );
        builder.displayName( JsonSerializerUtil.getStringValue( "displayName", contentTypeNode ) );
        if ( contentTypeNode.has( "contentDisplayNameScript" ) )
        {
            builder.contentDisplayNameScript( JsonSerializerUtil.getStringValue( "contentDisplayNameScript", contentTypeNode ) );
        }
        builder.superType( superType );
        builder.setAbstract( JsonSerializerUtil.getBooleanValue( "isAbstract", contentTypeNode ) );
        builder.setFinal( JsonSerializerUtil.getBooleanValue( "isFinal", contentTypeNode ) );
        builder.builtIn( JsonSerializerUtil.getBooleanValue( "isBuiltIn", contentTypeNode, false ) );
        builder.allowChildContent( JsonSerializerUtil.getBooleanValue( "allowChildContent", contentTypeNode, true ) );
        if ( includeCreatedTime )
        {
            builder.createdTime( JsonSerializerUtil.getDateTimeValue( "createdTime", contentTypeNode ) );
        }
        if ( includeModifiedTime )
        {
            builder.modifiedTime( JsonSerializerUtil.getDateTimeValue( "modifiedTime", contentTypeNode ) );
        }

        try
        {
            for ( FormItem formItem : formItemsSerializer.parse( contentTypeNode.get( "form" ) ) )
            {
                builder.addFormItem( formItem );
            }

        }
        catch ( Exception e )
        {
            throw new JsonParsingException( "Failed to parse content type: " + contentTypeNode.toString(), e );
        }

        return builder.build();
    }
}
