package com.enonic.wem.core.content.data;

import java.io.IOException;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonNode;

import com.enonic.wem.core.content.JsonParserUtil;
import com.enonic.wem.core.content.type.datatype.BasalValueType;

public class DataSerializerJson
{
    public static void generate( final Entry entry, final JsonGenerator g )
        throws IOException
    {
        final Data data = (Data) entry;

        g.writeStartObject();
        g.writeStringField( "path", data.getPath().toString() );
        if ( data.getBasalValueType() != null )
        {
            g.writeStringField( "type", data.getBasalValueType().toString() );
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

    public static Entry parse( JsonNode entryNode )
    {
        Data.Builder builder = Data.newBuilder();

        String pathAsString = JsonParserUtil.getStringValue( "path", entryNode );
        String valueAsString = JsonParserUtil.getStringValue( "value", entryNode );
        String typeAsString = JsonParserUtil.getStringValue( "type", entryNode, null );

        builder.path( new EntryPath( pathAsString ) );
        builder.value( valueAsString );
        if ( typeAsString != null )
        {
            builder.type( BasalValueType.valueOf( typeAsString ) );
        }

        return builder.build();
    }
}
