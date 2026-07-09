package com.enonic.xp.lib.schema;

import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.core.impl.schema.YmlMacroDescriptorParser;
import com.enonic.xp.macro.MacroDescriptor;
import com.enonic.xp.macro.MacroKey;
import com.enonic.xp.schema.SchemaResult;
import com.enonic.xp.schema.ListMacrosParams;
import com.enonic.xp.resource.Resource;

import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ListMacrosHandlerTest
    extends BaseSchemaHandlerTest
{
    @Test
    void testMacros()
    {
        when( schemaService.listMacros( isA( ListMacrosParams.class ) ) ).thenAnswer( params -> {
            final String resourceValue =
                "kind: \"Macro\"\n" + "title: \"My Macro\"\n" + "form:\n" + "- type: \"Double\"\n" + "  name: \"input\"\n" +
                    "  label: \"Input\"\n" + "  occurrences:\n" + "    min: 0\n" + "    max: 1\n" + "config:\n" +
                    "  provider: \"myprovider\"";

            final MacroDescriptor.Builder builder = YmlMacroDescriptorParser.parse( resourceValue, ApplicationKey.from( "myapp" ) );
            builder.key( MacroKey.from( "myapp:mymacro" ) );
            builder.modifiedTime( Instant.parse( "2021-02-25T10:44:33.170079900Z" ) );

            final Resource resource = mock( Resource.class );
            when( resource.readString() ).thenReturn( resourceValue );

            return List.of( new SchemaResult<>( builder.build(), resource ) );
        } );

        runScript( "/lib/xp/examples/schema/listMacros.js" );
    }
}
