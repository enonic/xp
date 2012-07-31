package com.enonic.wem.core.content.data;

import java.io.IOException;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonNode;

import com.enonic.wem.core.content.JsonParserUtil;
import com.enonic.wem.core.content.type.valuetype.BasalValueType;

public class ValueSerializerJson
{
    public static void generate( final Entry entry, final JsonGenerator g )
        throws IOException
    {
        final Value fieldValue = (Value) entry;

        g.writeStartObject();
        g.writeStringField( "path", fieldValue.getPath().toString() );
        if ( fieldValue.getBasalValueType() != null )
        {
            g.writeStringField( "type", fieldValue.getBasalValueType().toString() );
        }
        if ( fieldValue.getValue() != null )
        {
            g.writeStringField( "value", String.valueOf( fieldValue.getValue() ) );
        }
        else
        {
            g.writeNullField( "value " );
        }

        g.writeEndObject();
    }

    public static Entry parse( JsonNode entryNode )
    {
        Value.Builder builder = Value.newBuilder();

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
