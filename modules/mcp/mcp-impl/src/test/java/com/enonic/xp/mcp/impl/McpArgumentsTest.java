package com.enonic.xp.mcp.impl;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.portal.script.PortalScriptService;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.testing.ScriptTestSupport;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@NullMarked
class McpArgumentsTest
    extends ScriptTestSupport
{
    private static final DescriptorKey KEY = DescriptorKey.from( "myapplication:args" );

    private static final String ARGUMENTS = """
        {"int":42,"long":4102444800000,"unsafe":9007199254740993,"big":123456789012345678901234567890,\
        "double":1.5,"flag":true,"text":"Héllo","n":null,\
        "nested":{"deep":{"value":"x","empty":null}},"list":[1,null,["a",null],{"k":null}]}""";

    private static final String RECEIVED = """
        {"int":42,"long":4102444800000,"unsafe":9007199254740992,"big":1.2345678901234568e+29,\
        "double":1.5,"flag":true,"text":"Héllo","n":null,\
        "nested":{"deep":{"value":"x","empty":null}},"list":[1,null,["a",null],{"k":null}]}""";

    private static final String FACTS = """
        {"keys":["int","long","unsafe","big","double","flag","text","n","nested","list"],\
        "types":{"int":"number","long":"number","unsafe":"number","big":"number","double":"number",\
        "flag":"boolean","text":"string","nested":"object","list":true,"inner":true},\
        "explicitNull":true,"hasNull":true,"listLength":4,"listNull":true,"deep":"x","nestedNull":true,\
        "sum":4102444800043.5}""";

    @Test
    void controller_receives_arguments_as_plain_javascript_object()
        throws Exception
    {
        final Map<String, @Nullable Object> arguments = new ObjectMapper().readValue( ARGUMENTS, new TypeReference<>()
        {
        } );
        assertThat( arguments.get( "int" ) ).isInstanceOf( Integer.class );
        assertThat( arguments.get( "long" ) ).isInstanceOf( Long.class );
        assertThat( arguments.get( "unsafe" ) ).isInstanceOf( Long.class );
        assertThat( arguments.get( "big" ) ).isInstanceOf( BigInteger.class );
        assertThat( arguments.get( "double" ) ).isInstanceOf( Double.class );

        final var result = executor().execute( KEY, "args", arguments );

        assertThat( result.get( "content" ) ).isEqualTo(
            List.of( Map.of( "type", "text", "text", RECEIVED ), Map.of( "type", "text", "text", FACTS ) ) );
    }

    @Test
    void returned_undefined_becomes_null_and_array_nulls_are_dropped()
    {
        final var result = runScript( "/mcp/args/args.js" ).executeMethod( "limits" ).getMap();

        assertThat( result ).containsOnly( entry( "defined", "value" ), entry( "missing", null ), entry( "explicitNull", null ),
                                           entry( "list", List.of( "a", "b" ) ) );
    }

    private McpToolExecutorImpl executor()
    {
        final var controller = ResourceKey.from( KEY.getApplicationKey(), "/mcp/args/args.js" );
        final var descriptors = mock( McpDescriptorServiceImpl.class );
        when( descriptors.getControllerResourceKey( KEY ) ).thenReturn( controller );
        final var scripts = mock( PortalScriptService.class );
        when( scripts.hasScript( controller ) ).thenReturn( true );
        when( scripts.execute( controller ) ).thenAnswer( invocation -> runScript( controller ) );
        return new McpToolExecutorImpl( descriptors, scripts );
    }
}
