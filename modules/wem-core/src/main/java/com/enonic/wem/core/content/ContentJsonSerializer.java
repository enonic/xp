package com.enonic.wem.core.content;


import java.io.IOException;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;
import org.joda.time.DateTime;

import com.enonic.wem.api.account.AccountKey;
import com.enonic.wem.api.account.UserKey;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.type.QualifiedContentTypeName;
import com.enonic.wem.core.content.dao.ContentIdFactory;
import com.enonic.wem.core.content.data.ContentDataJsonSerializer;

import static com.enonic.wem.core.content.JsonParserUtil.getStringValue;

public class ContentJsonSerializer
    extends AbstractJsonSerializer<Content>
    implements ContentSerializer
{
    private ContentDataJsonSerializer contentDataSerializer = new ContentDataJsonSerializer();


    public ContentJsonSerializer()
    {
    }

    @Override
    public JsonNode serialize( final Content content, final ObjectMapper objectMapper )
    {
        final ObjectNode jsonContent = objectMapper.createObjectNode();

        jsonContent.put( "id", content.getId() == null ? null : content.getId().id() );
        jsonContent.put( "path", content.getPath().toString() );
        jsonContent.put( "name", content.getName() );
        jsonContent.put( "type", content.getType() != null ? content.getType().toString() : null );
        jsonContent.put( "displayName", content.getDisplayName() );
        jsonContent.put( "owner", content.getOwner() != null ? content.getOwner().toString() : null );
        jsonContent.put( "modifier", content.getModifier() != null ? content.getModifier().toString() : null );
        jsonContent.put( "modifiedTime", content.getModifiedTime() != null ? content.getModifiedTime().toString() : null );
        jsonContent.put( "createdTime", content.getCreatedTime() != null ? content.getCreatedTime().toString() : null );

        jsonContent.put( "data", contentDataSerializer.serialize( content.getData(), objectMapper ) );
        return jsonContent;
    }

    public Content toContent( final String json )
    {
        try
        {
            final JsonFactory f = JsonFactoryHolder.DEFAULT_FACTORY;
            final JsonParser jp = f.createJsonParser( json );

            final ObjectMapper mapper = new ObjectMapper();
            final JsonNode contentNode = mapper.readValue( jp, JsonNode.class );

            try
            {
                return parse( contentNode );
            }
            finally
            {
                jp.close();
            }

        }
        catch ( IOException e )
        {
            throw new RuntimeException( "Failed to read json", e );
        }
    }

    public Content parse( final JsonNode contentNode )
    {
        final Content.Builder contentBuilder = Content.newContent();
        final String name = getStringValue( "name", contentNode );
        if ( !StringUtils.isBlank( name ) )
        {
            contentBuilder.name( name );
        }

        final String typeAsString = getStringValue( "type", contentNode, null );
        if ( typeAsString != null )
        {
            final QualifiedContentTypeName qualifiedContentTypeName = new QualifiedContentTypeName( typeAsString );
            contentBuilder.type( qualifiedContentTypeName );
        }

        final String id = getStringValue( "id", contentNode, null );
        if ( id != null )
        {
            contentBuilder.id( ContentIdFactory.from( id ) );
        }
        contentBuilder.path( ContentPath.from( getStringValue( "path", contentNode, null ) ) );
        contentBuilder.displayName( getStringValue( "displayName", contentNode, null ) );
        contentBuilder.owner( getUserValue( contentNode, "owner" ) );
        contentBuilder.modifier( getUserValue( contentNode, "modifier" ) );
        contentBuilder.modifiedTime( getDateTimeValue( contentNode, "modifiedTime" ) );
        contentBuilder.createdTime( getDateTimeValue( contentNode, "createdTime" ) );

        contentBuilder.data( contentDataSerializer.parse( contentNode.get( "data" ) ) );

        return contentBuilder.build();
    }

    private UserKey getUserValue( final JsonNode node, final String propertyName )
    {
        final String value = getStringValue( propertyName, node, null );
        return value != null ? AccountKey.from( value ).asUser() : null;
    }

    private DateTime getDateTimeValue( final JsonNode node, final String propertyName )
    {
        final String value = getStringValue( propertyName, node, null );
        return value != null ? DateTime.parse( value ) : null;
    }
}
