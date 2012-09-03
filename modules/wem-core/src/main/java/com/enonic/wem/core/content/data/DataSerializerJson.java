package com.enonic.wem.core.content.data;

import java.io.IOException;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonNode;

import com.enonic.wem.core.content.JsonParserUtil;
import com.enonic.wem.core.content.datatype.DataTypes;
import com.enonic.wem.core.content.type.configitem.Field;

public class DataSerializerJson
{
    public void generate( final Entry entry, final JsonGenerator g )
        throws IOException
    {
        final Data data = (Data) entry;

        g.writeStartObject();
        g.writeStringField( "path", data.getPath().toString() );
        if ( data.getDataType() != null )
        {
            g.writeNumberField( "type", data.getDataType().getKey() );
        }
        if ( data.getValue() != null )
        {
            g.writeStringField( "value", String.valueOf( data.getValue() ) );
        }
        else
        {
            g.writeNullField( "value " );
        }

        g.writeEndObject();
    }

    public Entry parse( final JsonNode entryNode, final Field field )
    {
        Data.Builder builder = Data.newData();

        String pathAsString = JsonParserUtil.getStringValue( "path", entryNode );
        String valueAsString = JsonParserUtil.getStringValue( "value", entryNode );
        Integer typeAsInteger = JsonParserUtil.getIntegerValue( "type", entryNode, null );

        builder.path( new EntryPath( pathAsString ) );
        builder.value( valueAsString );
        if ( typeAsInteger != null )
        {
            builder.type( DataTypes.parse( typeAsInteger ) );
        }
        builder.field( field );

        return builder.build();
    }
}
