package com.enonic.wem.core.content.serializer;


import java.io.IOException;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.data.RootDataSet;
import com.enonic.wem.api.content.schema.type.QualifiedContentTypeName;
import com.enonic.wem.core.content.JsonFactoryHolder;
import com.enonic.wem.core.content.dao.ContentIdFactory;
import com.enonic.wem.core.support.serializer.AbstractJsonSerializer;

import static com.enonic.wem.core.support.serializer.JsonSerializerUtil.getDateTimeValue;
import static com.enonic.wem.core.support.serializer.JsonSerializerUtil.getStringValue;
import static com.enonic.wem.core.support.serializer.JsonSerializerUtil.getUserKeyValue;
import static com.enonic.wem.core.support.serializer.JsonSerializerUtil.setDateTimeValue;

public class ContentJsonSerializer
    extends AbstractJsonSerializer<Content>
    implements ContentSerializer
{
    private RootDataSetJsonSerializer rootDataSetSerializer;

    public ContentJsonSerializer()
    {
        this.rootDataSetSerializer = new RootDataSetJsonSerializer( objectMapper() );
    }

    public ContentJsonSerializer( final ObjectMapper objectMapper )
    {
        super( objectMapper );
        this.rootDataSetSerializer = new RootDataSetJsonSerializer( objectMapper );
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
        jsonContent.put( "modifier", content.getModifier() != null ? content.getModifier().toString() : null );
        setDateTimeValue( "modifiedTime", content.getModifiedTime(), jsonContent );
        setDateTimeValue( "createdTime", content.getCreatedTime(), jsonContent );

        jsonContent.put( "data", rootDataSetSerializer.serialize( content.getRootDataSet() ) );
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
        contentBuilder.owner( getUserKeyValue( "owner", contentNode ) );
        contentBuilder.modifier( getUserKeyValue( "modifier", contentNode ) );
        contentBuilder.modifiedTime( getDateTimeValue( "modifiedTime", contentNode ) );
        contentBuilder.createdTime( getDateTimeValue( "createdTime", contentNode ) );

        final RootDataSet rootDataSet = rootDataSetSerializer.parse( contentNode.get( "data" ) );
        contentBuilder.rootDataSet( rootDataSet );

        return contentBuilder.build();
    }


}
