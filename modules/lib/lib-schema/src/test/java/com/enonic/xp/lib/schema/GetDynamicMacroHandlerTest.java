package com.enonic.xp.lib.schema;

import java.nio.charset.StandardCharsets;
import java.time.Instant;

import org.junit.jupiter.api.Test;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.core.impl.schema.YmlMacroDescriptorParser;
import com.enonic.xp.icon.Icon;
import com.enonic.xp.macro.MacroDescriptor;
import com.enonic.xp.macro.MacroKey;
import com.enonic.xp.resource.DynamicSchemaResult;
import com.enonic.xp.resource.Resource;

import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GetDynamicMacroHandlerTest
    extends BaseSchemaHandlerTest
{
    static final String MACRO_RESOURCE = """
        kind: "Macro"
        title: "My Macro"
        form:
        - type: "Double"
          name: "input"
          label: "Input"
          occurrences:
            min: 0
            max: 1
        config:
          provider: "myprovider\"""";

    static DynamicSchemaResult<MacroDescriptor> macroResult( final String key )
    {
        final MacroKey macroKey = MacroKey.from( key );
        final MacroDescriptor.Builder builder = YmlMacroDescriptorParser.parse( MACRO_RESOURCE, macroKey.getApplicationKey() );
        builder.key( macroKey );
        builder.modifiedTime( Instant.parse( "2021-02-25T10:44:33.170079900Z" ) );
        builder.icon( Icon.from( "<svg/>".getBytes( StandardCharsets.UTF_8 ), "image/svg+xml", Instant.parse( "2021-02-25T10:44:33.170079900Z" ) ) );

        final Resource resource = mock( Resource.class );
        when( resource.readString() ).thenReturn( MACRO_RESOURCE );

        return new DynamicSchemaResult<>( builder.build(), resource );
    }

    @Test
    void testMacro()
    {
        when( dynamicSchemaService.getMacro( isA( MacroKey.class ) ) ).thenAnswer(
            params -> macroResult( params.getArgument( 0, MacroKey.class ).toString() ) );

        runScript( "/lib/xp/examples/schema/getMacro.js" );
    }

    @Test
    void testNull()
    {
        runFunction( "/test/GetDynamicMacroHandlerTest.js", "getNull" );
    }

    @Test
    void testNotFound()
    {
        when( dynamicSchemaService.getMacro( isA( MacroKey.class ) ) ).thenReturn( null );

        runFunction( "/test/GetDynamicMacroHandlerTest.js", "getNotFound" );
    }

    static ApplicationKey app()
    {
        return ApplicationKey.from( "myapp" );
    }
}
