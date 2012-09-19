package com.enonic.wem.core.content.data;

import java.io.IOException;
import java.util.Iterator;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonNode;

import com.google.common.base.Preconditions;

import com.enonic.wem.core.content.JsonParserUtil;
import com.enonic.wem.core.content.datatype.BaseDataType;
import com.enonic.wem.core.content.datatype.DataTypes;

import com.enonic.cms.framework.blob.BlobKey;

public class DataSerializerJson
{
    public void generate( final Data data, final JsonGenerator g )
        throws IOException
    {
        g.writeStartObject();
        g.writeStringField( "name", data.getPath().getLastElement().toString() );
        if ( data.getDataType() != null )
        {
            g.writeStringField( "type", data.getDataType().getName() );
        }
        if ( data.getValue() != null )
        {
            if ( data.getDataType().equals( DataTypes.DATA_SET ) )
            {
                final DataSet dataSet = (DataSet) data.getValue();
                g.writeArrayFieldStart( "value" );
                for ( final Data e : dataSet )
                {
                    generate( e, g );
                }
                g.writeEndArray();
            }
            else
            {
                if ( data.getDataType().equals( DataTypes.BLOB ) )
                {
                    Preconditions.checkArgument( data.getValue() instanceof BlobKey,
                                                 "Data at path [%s] of type BLOB needs to have a BlobKey as value before it is serialized: " +
                                                     data.getValue().getClass(), data.getPath() );
                }
                g.writeStringField( "value", String.valueOf( data.getValue() ) );
            }
        }
        else
        {
            g.writeNullField( "value " );
        }

        g.writeEndObject();
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
        else
        {
            final String valueAsString = JsonParserUtil.getStringValue( "value", dataNode );
            builder.value( valueAsString );
        }

        return builder.build();
    }
}
