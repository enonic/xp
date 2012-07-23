package com.enonic.wem.core.content.config.field;


import java.io.IOException;

import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonToken;

import com.enonic.wem.core.content.config.field.type.FieldTypeJsonParser;

public class FieldJsonParser
{
    public static Field parse( JsonParser jp, ConfigItems configItems )
        throws IOException
    {

        Field.Builder builder = Field.newBuilder();

        JsonToken token = jp.nextValue();
        while ( token != JsonToken.END_OBJECT )
        {
            if ( "name".equals( jp.getCurrentName() ) && token == JsonToken.VALUE_STRING )
            {
                builder.name( jp.getText() );
            }
            else if ( "displayName".equals( jp.getCurrentName() ) )
            {
                builder.label( jp.getText() );
            }
            else if ( "required".equals( jp.getCurrentName() ) )
            {
                builder.required( jp.getBooleanValue() );
            }
            else if ( "immutable".equals( jp.getCurrentName() ) )
            {
                builder.immutable( jp.getBooleanValue() );
            }
            else if ( "indexed".equals( jp.getCurrentName() ) )
            {
                builder.indexed( jp.getBooleanValue() );
            }
            else if ( "customText".equals( jp.getCurrentName() ) )
            {
                builder.customText( jp.getText() );
            }
            else if ( "type".equals( jp.getCurrentName() ) )
            {
                builder.type( FieldTypeJsonParser.parse( jp ) );
            }
            else if ( "fieldConfig".equals( jp.getCurrentName() ) )
            {

                do
                {
                    System.out.println( "Unhandled field config:" );
                    System.out.println( "..." + token );
                    System.out.println( "... name: " + jp.getCurrentName() );
                    System.out.println( "... text: " + jp.getText() );
                    jp.nextValue();
                }
                while ( jp.getCurrentToken() != JsonToken.END_ARRAY );
                jp.nextToken();
                //builder.type( FieldTypeParser.parse( jp ) );
            }
            else
            {
                System.out.println( "Unhandled:" );
                System.out.println( "..." + token );
                System.out.println( "... name: " + jp.getCurrentName() );
                System.out.println( "... text: " + jp.getText() );
            }
            token = jp.nextValue();
        }

        return builder.build();
    }
}
