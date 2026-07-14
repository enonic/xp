package com.enonic.xp.lib.schema;

import java.time.Instant;

import org.junit.jupiter.api.Test;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.core.impl.schema.YmlMacroDescriptorParser;
import com.enonic.xp.macro.MacroDescriptor;
import com.enonic.xp.macro.MacroKey;
import com.enonic.xp.resource.DynamicSchemaResult;
import com.enonic.xp.resource.GetDynamicMacroParams;
import com.enonic.xp.resource.Resource;

import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GetDynamicMacroHandlerTest
    extends BaseSchemaHandlerTest
{
    @Test
    void testMacro()
    {
        when( dynamicSchemaService.getMacro( isA( GetDynamicMacroParams.class ) ) ).thenAnswer( params -> {
            final String resourceValue =
                "kind: \"Macro\"\n" + "title: \"My Macro\"\n" + "form:\n" + "- type: \"Double\"\n" + "  name: \"input\"\n" +
                    "  label: \"Input\"\n" + "  occurrences:\n" + "    min: 0\n" + "    max: 1\n" + "config:\n" +
                    "  provider: \"myprovider\"";

            final MacroDescriptor.Builder builder = YmlMacroDescriptorParser.parse( resourceValue, ApplicationKey.from( "myapp" ) );
            builder.key( MacroKey.from( "myapp:mymacro" ) );
            builder.modifiedTime( Instant.parse( "2021-02-25T10:44:33.170079900Z" ) );

            final Resource resource = mock( Resource.class );
            when( resource.readString() ).thenReturn( resourceValue );

            return new DynamicSchemaResult<>( builder.build(), resource );
        } );

        runScript( "/lib/xp/examples/schema/getMacro.js" );
    }

    @Test
    void testNull()
    {
        runFunction( "/test/GetDynamicMacroHandlerTest.js", "getNull" );
    }
}
