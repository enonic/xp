package com.enonic.wem.core.content.data;

import java.util.Iterator;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.blob.BlobKey;
import com.enonic.wem.api.content.data.Data;
import com.enonic.wem.api.content.data.DataArray;
import com.enonic.wem.api.content.data.DataSet;
import com.enonic.wem.api.content.data.EntryPath;
import com.enonic.wem.api.content.datatype.BaseDataType;
import com.enonic.wem.api.content.datatype.DataTypes;
import com.enonic.wem.core.content.JsonParserUtil;


public class DataSerializerJson
{
    public JsonNode generate( final Data data, final ObjectMapper objectMapper )
    {
        final ObjectNode jsonData = objectMapper.createObjectNode();
        final String name = data.getPath().resolveComponentPath().getLastElement();
        jsonData.put( "name", name );
        if ( data.getDataType() != null )
        {
            jsonData.put( "type", data.getDataType().getName() );
        }
        if ( data.getValue() != null )
        {
            if ( data.getDataType().equals( DataTypes.DATA_SET ) )
            {
                final DataSet dataSet = data.getDataSet();
                final ArrayNode jsonDataValue = jsonData.putArray( "value" );
                for ( final Data e : dataSet )
                {
                    jsonDataValue.add( generate( e, objectMapper ) );
                }
            }
            else if ( data.getDataType().equals( DataTypes.DATA_ARRAY ) )
            {
                final DataArray dataArray = data.getDataArray();
                final ArrayNode jsonDataValue = jsonData.putArray( "value" );
                for ( final Data e : dataArray )
                {
                    jsonDataValue.add( generate( e, objectMapper ) );
                }
            }
            else
            {
                if ( data.getDataType().equals( DataTypes.BLOB ) )
                {
                    Preconditions.checkArgument( data.getValue() instanceof BlobKey,
                                                 "Data at path [%s] of type BLOB needs to have a BlobKey as value before it is serialized: " +
                                                     data.getValue().getClass(), data.getPath() );
                }
                jsonData.put( "value", String.valueOf( data.getValue() ) );
            }
        }
        else
        {
            jsonData.putNull( "value" );
        }

        return jsonData;
    }

    public Data parse( final EntryPath parentPath, final JsonNode dataNode )
    {
        final Data.Builder builder = Data.newData();

        final EntryPath entryPath = new EntryPath( parentPath, JsonParserUtil.getStringValue( "name", dataNode ) );
        builder.path( entryPath );
        final BaseDataType type = (BaseDataType) DataTypes.parseByName( JsonParserUtil.getStringValue( "type", dataNode, null ) );
        Preconditions.checkNotNull( type, "type was null" );
        builder.type( type );
        if ( type.equals( DataTypes.DATA_SET ) )
        {
            final DataSet dataSet = new DataSet( entryPath );
            builder.value( dataSet );
            final JsonNode valueNode = dataNode.get( "value" );
            final Iterator<JsonNode> dataIt = valueNode.getElements();
            while ( dataIt.hasNext() )
            {
                final JsonNode eNode = dataIt.next();
                dataSet.add( parse( entryPath, eNode ) );
            }
        }
        else if ( type.equals( DataTypes.DATA_ARRAY ) )
        {
            final DataArray array = new DataArray( entryPath );
            builder.value( array );
            final JsonNode valueNode = dataNode.get( "value" );
            final Iterator<JsonNode> dataIt = valueNode.getElements();
            while ( dataIt.hasNext() )
            {
                final JsonNode eNode = dataIt.next();
                array.add( parse( parentPath, eNode ) );
            }
        }
        else
        {
            final String valueAsString = JsonParserUtil.getStringValue( "value", dataNode );
            builder.value( valueAsString );
        }

        return builder.build();
    }
}
