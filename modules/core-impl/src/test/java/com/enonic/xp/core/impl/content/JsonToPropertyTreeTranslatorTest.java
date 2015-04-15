package com.enonic.xp.core.impl.content;

import java.net.URL;

import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.enonic.xp.data.Property;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.data.ValueTypes;
import com.enonic.xp.form.Form;
import com.enonic.xp.form.Input;
import com.enonic.xp.form.inputtype.DateTimeConfig;
import com.enonic.xp.form.inputtype.InputTypes;

import static org.junit.Assert.*;

public class JsonToPropertyTreeTranslatorTest
{
    final ObjectMapper mapper = new ObjectMapper();

    @Test
    public void map_array_values()
        throws Exception
    {
        final JsonNode node = loadJson( "traverseTest" );

        final PropertyTree data = JsonToPropertyTreeTranslator.create().
            mode( JsonToPropertyTreeTranslator.Mode.LENIENT ).
            build().
            translate( node );

        final Property myArray = data.getProperty( "myArray" );
        assertNotNull( myArray );
        assertEquals( ValueTypes.STRING.getName(), myArray.getType().getName() );

        final Property myArray0 = data.getProperty( "myArray[0]" );
        assertNotNull( myArray0 );

        final Property myArray1 = data.getProperty( "myArray[1]" );
        assertNotNull( myArray1 );

        final Property myArray2 = data.getProperty( "myArray[2]" );
        assertNotNull( myArray2 );
    }

    @Test
    public void map_dateTime()
        throws Exception
    {
        final JsonNode node = loadJson( "traverseTest" );

        final Form form = Form.newForm().
            addFormItem( Input.create().
                name( "noTimezone" ).
                inputType( InputTypes.DATE_TIME ).
                inputTypeConfig( DateTimeConfig.create().
                    withTimezone( false ).
                    build() ).
                build() ).
            addFormItem( Input.create().
                name( "timezoned" ).
                inputType( InputTypes.DATE_TIME ).
                inputTypeConfig( DateTimeConfig.create().
                    withTimezone( true ).
                    build() ).
                build() ).
            build();

        final PropertyTree data = JsonToPropertyTreeTranslator.create().
            form( form ).
            mode( JsonToPropertyTreeTranslator.Mode.LENIENT ).
            build().
            translate( node );

        final Property noTimezone = data.getProperty( "noTimezone" );
        assertNotNull( noTimezone );
        assertEquals( ValueTypes.LOCAL_DATE_TIME.getName(), noTimezone.getType().getName() );

        final Property timezoned = data.getProperty( "timezoned" );
        assertNotNull( timezoned );
        assertEquals( ValueTypes.DATE_TIME.getName(), timezoned.getType().getName() );
    }


    protected final JsonNode loadJson( final String name )
        throws Exception
    {
        final String resource = "/" + getClass().getName().replace( '.', '/' ) + "-" + name + ".json";
        final URL url = getClass().getResource( resource );

        assertNotNull( "File [" + resource + "] not found", url );
        return this.mapper.readTree( url );
    }


}

