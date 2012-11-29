package com.enonic.wem.core.content.type;


import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;

import com.enonic.wem.api.content.type.ContentType;
import com.enonic.wem.api.content.type.QualifiedContentTypeName;
import com.enonic.wem.api.content.type.form.FormItem;
import com.enonic.wem.api.content.type.form.FormItems;
import com.enonic.wem.api.module.Module;
import com.enonic.wem.core.content.AbstractJsonSerializer;
import com.enonic.wem.core.content.JsonParserUtil;
import com.enonic.wem.core.content.JsonParsingException;
import com.enonic.wem.core.content.type.form.FormItemsJsonSerializer;

import static com.enonic.wem.api.content.type.ContentType.newContentType;

public class ContentTypeJsonSerializer
    extends AbstractJsonSerializer<ContentType>
    implements ContentTypeSerializer
{
    private FormItemsJsonSerializer formItemsSerializer = new FormItemsJsonSerializer();

    @Override
    protected JsonNode serialize( final ContentType contentType, final ObjectMapper objectMapper )
    {
        final ObjectMapper mapper = new ObjectMapper();
        final ObjectNode objectNode = mapper.createObjectNode();
        objectNode.put( "name", contentType.getName() );
        objectNode.put( "module", contentType.getModule().getName() );
        objectNode.put( "qualifiedName", contentType.getQualifiedName().toString() );
        objectNode.put( "displayName", contentType.getDisplayName() );
        objectNode.put( "superType", contentType.getSuperType() != null ? contentType.getSuperType().toString() : null );
        objectNode.put( "isAbstract", contentType.isAbstract() );
        objectNode.put( "isFinal", contentType.isFinal() );
        objectNode.put( "form", formItemsSerializer.serialize( contentType.form().getFormItems(), mapper ) );
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

        final ContentType.Builder contentTypeBuilder = newContentType().
            name( JsonParserUtil.getStringValue( "name", contentTypeNode ) ).
            module( new Module( JsonParserUtil.getStringValue( "module", contentTypeNode ) ) ).
            displayName( JsonParserUtil.getStringValue( "displayName", contentTypeNode ) ).
            superType( superType ).
            setAbstract( JsonParserUtil.getBooleanValue( "isAbstract", contentTypeNode ) ).
            setFinal( JsonParserUtil.getBooleanValue( "isFinal", contentTypeNode ) );

        try
        {
            final FormItems formItems = formItemsSerializer.parse( contentTypeNode.get( "form" ) );
            for ( FormItem formItem : formItems )
            {
                contentTypeBuilder.addFormItem( formItem );
            }

        }
        catch ( Exception e )
        {
            throw new JsonParsingException( "Failed to parse content type: " + contentTypeNode.toString(), e );
        }

        return contentTypeBuilder.build();
    }
}
