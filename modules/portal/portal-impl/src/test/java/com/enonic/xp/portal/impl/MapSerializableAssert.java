package com.enonic.xp.portal.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import com.enonic.xp.script.ScriptFixturesFacade;
import com.enonic.xp.script.ScriptValue;
import com.enonic.xp.script.impl.value.ScriptValueFactory;
import com.enonic.xp.script.serializer.MapSerializable;

import static org.junit.jupiter.api.Assertions.assertEquals;

public final class MapSerializableAssert
{
    private static final ScriptValueFactory<?> SCRIPT_VALUE_FACTORY = ScriptFixturesFacade.getInstance().scriptValueFactory();

    private final Class<?> clazz;

    public MapSerializableAssert( Class<?> clazz )
    {
        this.clazz = clazz;
    }

    public void assertJson( final String resource, final MapSerializable value )
    {
        assertEquals( readJsonFromResource( clazz, resource ), serializeJson( value ) );
    }

    public static String readJsonFromResource( final Class<?> clazz, final String resource )
    {
        final ScriptValue jsonReStringify = SCRIPT_VALUE_FACTORY.evalValue( "(s) => JSON.stringify(JSON.parse(s))" );
        return jsonReStringify.call( readFromFile( clazz, resource ) ).getValue( String.class );
    }

    public static String serializeJson( final MapSerializable value )
    {
        final ScriptValue jsonStringify = SCRIPT_VALUE_FACTORY.evalValue( "JSON.stringify" );
        return jsonStringify.call( value ).getValue( String.class );
    }

    public static ScriptValue serializeJs( final MapSerializable value )
    {
        return SCRIPT_VALUE_FACTORY.newValue( SCRIPT_VALUE_FACTORY.getJavascriptHelper().objectConverter().toJs( value ) );
    }

    private static String readFromFile( final Class<?> clazz, final String resource )
    {
        final InputStream stream =
            Objects.requireNonNull( clazz.getResourceAsStream( resource ), "Resource file [" + resource + "] not found" );
        try (stream)
        {
            return new String( stream.readAllBytes(), StandardCharsets.UTF_8 );
        }
        catch ( IOException e )
        {
            throw new UncheckedIOException( e );
        }
    }
}
