package com.enonic.wem.core.content.serializer;


import java.io.IOException;

import org.apache.commons.lang.StringUtils;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.data.ContentData;
import com.enonic.wem.api.schema.content.QualifiedContentTypeName;
import com.enonic.wem.core.content.JsonFactoryHolder;
import com.enonic.wem.core.support.serializer.AbstractJsonSerializer;

import static com.enonic.wem.core.support.serializer.JsonSerializerUtil.getDateTimeValue;
import static com.enonic.wem.core.support.serializer.JsonSerializerUtil.getStringValue;
import static com.enonic.wem.core.support.serializer.JsonSerializerUtil.getUserKeyValue;
import static com.enonic.wem.core.support.serializer.JsonSerializerUtil.setDateTimeValue;

public class ContentJsonSerializer
    extends AbstractJsonSerializer<Content>
    implements ContentSerializer
{
    private ContentDataJsonSerializer contentDataSerializer;

    public ContentJsonSerializer()
    {
        this.contentDataSerializer = new ContentDataJsonSerializer( objectMapper() );
    }

    public ContentJsonSerializer( final ObjectMapper objectMapper )
    {
        super( objectMapper );
        this.contentDataSerializer = new ContentDataJsonSerializer( objectMapper );
    }

    @Override
    public JsonNode serialize( final Content content )
    {
        final ObjectNode jsonContent = objectMapper().createObjectNode();

        jsonContent.put( "id", content.getId() == null ? null : content.getId().toString() );
        jsonContent.put( "path", content.getPath().toString() );
        jsonContent.put( "name", content.getName() );
        jsonContent.put( "type", content.getType() != null ? content.getType().toString() : null );
        jsonContent.put( "displayName", content.getDisplayName() );
        jsonContent.put( "owner", content.getOwner() != null ? content.getOwner().toString() : null );
        jsonContent.put( "creator", content.getCreator() != null ? content.getCreator().toString() : null );
        jsonContent.put( "modifier", content.getModifier() != null ? content.getModifier().toString() : null );
        jsonContent.put( "isRoot", content.getPath().isRoot() );
        setDateTimeValue( "modifiedTime", content.getModifiedTime(), jsonContent );
        setDateTimeValue( "createdTime", content.getCreatedTime(), jsonContent );

        jsonContent.put( "data", contentDataSerializer.serialize( content.getContentData() ) );
        return jsonContent;
    }

    public Content toContent( final String json )
    {
        try
        {
            final JsonFactory f = JsonFactoryHolder.DEFAULT_FACTORY;
            final JsonParser jp = f.createParser( json );

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
            contentBuilder.id( ContentId.from( id ) );
        }
        contentBuilder.path( ContentPath.from( getStringValue( "path", contentNode, null ) ) );
        contentBuilder.displayName( getStringValue( "displayName", contentNode, null ) );
        contentBuilder.owner( getUserKeyValue( "owner", contentNode ) );
        contentBuilder.creator( getUserKeyValue( "creator", contentNode ) );
        contentBuilder.modifier( getUserKeyValue( "modifier", contentNode ) );
        contentBuilder.modifiedTime( getDateTimeValue( "modifiedTime", contentNode ) );
        contentBuilder.createdTime( getDateTimeValue( "createdTime", contentNode ) );

        final ContentData contentData = contentDataSerializer.parse( contentNode.get( "data" ) );
        contentBuilder.contentData( contentData );

        return contentBuilder.build();
    }


}
