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
import com.enonic.wem.core.content.AbstractJsonSerializer;
import com.enonic.wem.core.content.JsonParserUtil;
import com.enonic.wem.core.content.JsonParsingException;

final class DataJsonSerializer
    extends AbstractJsonSerializer<Data>
{
    private static final String DATA_VALUE = "value";

    private static final String DATA_NAME = "name";

    private static final String DATA_TYPE = "type";

    private static final String DATA_PATH = "path";

    protected final JsonNode serialize( final Data data, final ObjectMapper objectMapper )
    {
        final ObjectNode dataObj = objectMapper.createObjectNode();
        final String name = data.getPath().resolveFormItemPath().getLastElement();
        dataObj.put( DATA_NAME, name );
        final String path = data.getPath().toString();
        dataObj.put( DATA_PATH, path );

        if ( data.getValue() != null )
        {
            if ( data.getDataType().equals( DataTypes.SET ) )
            {
                dataObj.put( DATA_TYPE, data.getDataType().getName() );
                dataObj.put( DATA_VALUE, serializeDataSet( data.getDataSet() ) );
            }
            else if ( data.getDataType().equals( DataTypes.ARRAY ) )
            {
                final DataArray dataArray = data.getDataArray();
                dataObj.put( DATA_TYPE, dataArray.getType().getName() );
                dataObj.put( DATA_VALUE, serializeDataArray( objectMapper, dataArray ) );
            }
            else
            {
                dataObj.put( DATA_TYPE, data.getDataType().getName() );
                if ( data.getDataType().equals( DataTypes.BLOB ) )
                {
                    Preconditions.checkArgument( data.getValue() instanceof BlobKey,
                                                 "Data at path [%s] of type BLOB needs to have a BlobKey as value before it is serialized: " +
                                                     data.getValue().getClass(), data.getPath() );
                }
                dataObj.put( DATA_VALUE, String.valueOf( data.getValue() ) );
            }
        }
        else
        {
            dataObj.putNull( DATA_VALUE );
        }

        return dataObj;
    }

    private ArrayNode serializeDataSet( final DataSet dataSet )
    {
        final ArrayNode dataSetArray = objectMapper().createArrayNode();
        for ( final Data data : dataSet )
        {
            dataSetArray.add( serialize( data, objectMapper() ) );
        }
        return dataSetArray;
    }

    private ArrayNode serializeDataArray( final ObjectMapper objectMapper, final DataArray dataArray )
    {
        final ArrayNode arrayNode = objectMapper.createArrayNode();
        for ( final Data data : dataArray )
        {
            if ( !data.hasArrayAsValue() && !data.hasDataSetAsValue() )
            {
                arrayNode.add( data.getString() );
            }
            else if ( data.hasDataSetAsValue() )
            {
                arrayNode.add( serializeDataSet( data.getDataSet() ) );
            }
        }
        return arrayNode;
    }

    protected final Data parse( final JsonNode dataNode )
    {
        final Data.Builder dataBuilder = Data.newData();

        final EntryPath path = new EntryPath( JsonParserUtil.getStringValue( DATA_PATH, dataNode ) );
        dataBuilder.path( path );

        final BaseDataType dataType = (BaseDataType) DataTypes.parseByName( JsonParserUtil.getStringValue( DATA_TYPE, dataNode, null ) );
        Preconditions.checkNotNull( dataType, "dataType was null" );

        final JsonNode valueNode = dataNode.get( DATA_VALUE );
        if ( valueNode.isArray() )
        {
            final ArrayNode valueArrayNode = (ArrayNode) valueNode;
            if ( valueArrayNode.size() > 0 )
            {
                if ( valueArrayNode.get( 0 ).isObject() )
                {
                    dataBuilder.type( DataTypes.SET );
                    final DataSet dataSet = new DataSet( path );
                    parseDataSet( valueArrayNode, dataSet );
                    dataBuilder.value( dataSet );
                }
                else if ( valueArrayNode.get( 0 ).isArray() )
                {
                    dataBuilder.type( DataTypes.ARRAY );
                    final DataArray array = new DataArray( path, dataType );
                    parseDataArray( valueArrayNode, array );
                    dataBuilder.value( array );
                }
                else if ( valueArrayNode.get( 0 ).isValueNode() )
                {
                    dataBuilder.type( DataTypes.ARRAY );
                    final DataArray array = new DataArray( path, dataType );
                    parseDataArray( valueArrayNode, array );
                    dataBuilder.value( array );
                }
                else
                {
                    throw new JsonParsingException( "Node type not supported: " + valueArrayNode.get( 0 ) );
                }
            }
        }
        else
        {
            dataBuilder.type( dataType );
            dataBuilder.value( valueNode.getTextValue() );
        }

        return dataBuilder.build();
    }

    private void parseDataSet( final ArrayNode arrayNode, final DataSet dataSet )
    {
        final Iterator<JsonNode> dataIt = arrayNode.getElements();
        while ( dataIt.hasNext() )
        {
            final JsonNode dataNode = dataIt.next();
            dataSet.add( parse( dataNode ) );
        }
    }


    private void parseDataArray( final ArrayNode arrayNode, final DataArray dataArray )
    {
        final Iterator<JsonNode> dataNodeIterator = arrayNode.getElements();
        while ( dataNodeIterator.hasNext() )
        {
            final JsonNode dataNode = dataNodeIterator.next();
            if ( dataNode.isValueNode() )
            {
                dataArray.add( dataNode.getTextValue() );
            }
            else if ( dataNode.isArray() && dataArray.getType().equals( DataTypes.SET ) )
            {
                final DataSet dataSet = new DataSet( dataArray.getPath() );
                dataArray.add( dataSet );
                parseDataSet( (ArrayNode) dataNode, dataSet );
            }
        }
    }
}
