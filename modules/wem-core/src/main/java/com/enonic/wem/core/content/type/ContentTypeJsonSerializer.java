package com.enonic.wem.core.content.type;


import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;
import org.joda.time.DateTime;

import com.enonic.wem.api.content.type.ContentType;
import com.enonic.wem.api.content.type.QualifiedContentTypeName;
import com.enonic.wem.api.content.type.form.FormItem;
import com.enonic.wem.api.content.type.form.FormItems;
import com.enonic.wem.api.module.ModuleName;
import com.enonic.wem.core.content.AbstractJsonSerializer;
import com.enonic.wem.core.content.JsonParserUtil;
import com.enonic.wem.core.content.JsonParsingException;
import com.enonic.wem.core.content.type.form.FormItemsJsonSerializer;

import static com.enonic.wem.api.content.type.ContentType.newContentType;

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
        objectNode.put( "name", contentType.getName() );
        objectNode.put( "module", contentType.getModuleName().toString() );
        objectNode.put( "qualifiedName", contentType.getQualifiedName().toString() );
        objectNode.put( "displayName", contentType.getDisplayName() );
        objectNode.put( "superType", contentType.getSuperType() != null ? contentType.getSuperType().toString() : null );
        objectNode.put( "isAbstract", contentType.isAbstract() );
        objectNode.put( "isFinal", contentType.isFinal() );
        if ( includeCreatedTime )
        {
            JsonParserUtil.setDateTimeValue( "createdTime", contentType.getCreatedTime(), objectNode );
        }
        if ( includeModifiedTime )
        {
            JsonParserUtil.setDateTimeValue( "modifiedTime", contentType.getModifiedTime(), objectNode );
        }
        if ( contentType.form() == null )
        {
            objectNode.putNull( "form" );
        }
        else
        {
            objectNode.put( "form", formItemsSerializer.serialize( contentType.form().getFormItems() ) );
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
        final String superTypeValue = JsonParserUtil.getStringValue( "superType", contentTypeNode );
        final QualifiedContentTypeName superType = superTypeValue != null ? new QualifiedContentTypeName( superTypeValue ) : null;

        final ContentType.Builder builder = newContentType();
        builder.name( JsonParserUtil.getStringValue( "name", contentTypeNode ) );
        builder.module( ModuleName.from( JsonParserUtil.getStringValue( "module", contentTypeNode ) ) );
        builder.displayName( JsonParserUtil.getStringValue( "displayName", contentTypeNode ) );
        builder.superType( superType );
        builder.setAbstract( JsonParserUtil.getBooleanValue( "isAbstract", contentTypeNode ) );
        builder.setFinal( JsonParserUtil.getBooleanValue( "isFinal", contentTypeNode ) );
        if ( includeCreatedTime )
        {
            if ( contentTypeNode.has( "createdTime" ) )
            {
                // TODO check necessary to handle old json data, should be remove later on
                builder.createdTime( JsonParserUtil.getDateTimeValue( "createdTime", contentTypeNode ) );
            }
            else
            {
                builder.createdTime( DateTime.now() );
            }
        }
        if ( includeModifiedTime )
        {
            if ( contentTypeNode.has( "modifiedTime" ) )
            {
                // TODO check necessary to handle old json data, should be remove later on
                builder.modifiedTime( JsonParserUtil.getDateTimeValue( "modifiedTime", contentTypeNode ) );
            }
            else
            {
                builder.modifiedTime( DateTime.now() );
            }
        }

        try
        {
            final FormItems formItems = formItemsSerializer.parse( contentTypeNode.get( "form" ) );
            for ( FormItem formItem : formItems )
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
