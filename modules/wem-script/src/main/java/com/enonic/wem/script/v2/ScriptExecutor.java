package com.enonic.wem.script.v2;

import java.net.URL;
import java.util.Map;
import java.util.function.Function;

import javax.script.Bindings;
import javax.script.ScriptEngine;

import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Charsets;
import com.google.common.collect.Maps;
import com.google.common.io.Resources;

import jdk.nashorn.api.scripting.NashornScriptEngineFactory;

import com.enonic.wem.script.serializer.MapGenerator;
import com.enonic.wem.script.serializer.MapSerializable;
import com.enonic.wem.script.v2.logger.LoggerScriptObject;

/**
 * ScriptValue
 * - getValue()
 * - getValue(key)
 * - getMap()
 * - getArray()
 * - getMap(key)
 * - getArray(key)
 * - isMap()
 * - isArray()
 */
public final class ScriptExecutor
{
    public final static class TestObject
    {
        @JsonProperty("n")
        public String name;

        @JsonProperty("f")
        public Function func;
    }

    private final ScriptEngine engine;

    public ScriptExecutor()
    {
        final NashornScriptEngineFactory factory = new NashornScriptEngineFactory();
        this.engine = factory.getScriptEngine();
    }

    public void execute()
        throws Exception
    {
        final URL url = getClass().getResource( "global.js" );
        final String script = Resources.toString( url, Charsets.UTF_8 );

        final Bindings bindings = this.engine.createBindings();
        bindings.put( "log", new LoggerScriptObject( LoggerFactory.getLogger( "test" ) ) );

        final Map<String, Object> map = Maps.newHashMap();
        map.put( "test", 3 );
        bindings.put( "other", (MapSerializable) this::generateMap );

        this.engine.eval( script, bindings );
    }

    private void generateMap( final MapGenerator generator )
    {
        generator.value( "a", 1 );
        generator.value( "b", 2 );
    }

    public static void main( final String... args )
        throws Exception
    {
        new ScriptExecutor().execute();
    }
}
