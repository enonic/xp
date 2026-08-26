package com.enonic.xp.lib.schema;

import java.time.Instant;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import com.enonic.xp.core.impl.schema.YmlMacroDescriptorParser;
import com.enonic.xp.icon.IconUpdate;
import com.enonic.xp.macro.MacroDescriptor;
import com.enonic.xp.macro.MacroKey;
import com.enonic.xp.resource.CreateOrUpdateMacroParams;
import com.enonic.xp.resource.DynamicSchemaResult;
import com.enonic.xp.resource.Resource;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CreateOrUpdateMacroHandlerTest
    extends BaseSchemaHandlerTest
{
    private static final Instant MODIFIED_TIME = Instant.parse( "2021-09-25T10:00:00.00Z" );

    @Test
    void testMacro()
    {
        when( dynamicSchemaService.createOrUpdateMacro( isA( CreateOrUpdateMacroParams.class ) ) ).thenAnswer( params -> {
            final CreateOrUpdateMacroParams macroParams = params.getArgument( 0, CreateOrUpdateMacroParams.class );
            final MacroDescriptor.Builder builder =
                YmlMacroDescriptorParser.parse( macroParams.getResource(), macroParams.getKey().getApplicationKey() );
            builder.key( macroParams.getKey() );
            builder.modifiedTime( MODIFIED_TIME );

            final Resource resource = mock( Resource.class );
            when( resource.readString() ).thenReturn( macroParams.getResource() );

            return new DynamicSchemaResult<>( builder.build(), resource );
        } );

        runScript( "/lib/xp/examples/schema/createOrUpdateMacro.js" );

        final ArgumentCaptor<CreateOrUpdateMacroParams> captor = ArgumentCaptor.forClass( CreateOrUpdateMacroParams.class );
        verify( dynamicSchemaService ).createOrUpdateMacro( captor.capture() );
        assertEquals( MacroKey.from( "myapp:mymacro" ), captor.getValue().getKey() );
        assertEquals( IconUpdate.KEEP, captor.getValue().getIcon() );
    }

    // `{data, mimeType}` without a resource: the stored descriptor is kept, the icon alone is replaced
    @Test
    void testMacroIcon()
        throws Exception
    {
        when( dynamicSchemaService.createOrUpdateMacro( isA( CreateOrUpdateMacroParams.class ) ) ).thenAnswer( params -> {
            final CreateOrUpdateMacroParams macroParams = params.getArgument( 0, CreateOrUpdateMacroParams.class );
            final MacroDescriptor.Builder builder = MacroDescriptor.create().key( macroParams.getKey() ).modifiedTime( MODIFIED_TIME );

            final Resource resource = mock( Resource.class );
            when( resource.readString() ).thenReturn( "kind: \"Macro\"" );

            return new DynamicSchemaResult<>( builder.build(), resource );
        } );

        runFunction( "/test/CreateOrUpdateMacroHandlerTest.js", "createOrUpdateMacroIcon" );

        final ArgumentCaptor<CreateOrUpdateMacroParams> captor = ArgumentCaptor.forClass( CreateOrUpdateMacroParams.class );
        verify( dynamicSchemaService ).createOrUpdateMacro( captor.capture() );
        assertNull( captor.getValue().getResource() );

        final IconUpdate.Replace replace = (IconUpdate.Replace) captor.getValue().getIcon();
        assertEquals( "image/svg+xml", replace.mimeType() );
        assertArrayEquals( "<svg/>".getBytes(), replace.data().read() );
    }

    @Test
    void testInvalidMacro()
    {
        runFunction( "/test/CreateOrUpdateMacroHandlerTest.js", "createInvalidMacro" );
    }
}