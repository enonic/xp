package com.enonic.xp.lib.schema;

import java.time.Instant;

import org.junit.jupiter.api.Test;

import com.enonic.xp.resource.CreateDynamicStylesParams;
import com.enonic.xp.resource.DynamicSchemaResult;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.style.StyleDescriptor;
import com.enonic.xp.xml.parser.XmlStyleDescriptorParser;

import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CreateDynamicStylesHandlerTest
    extends BaseSchemaHandlerTest
{
    @Test
    void testStyles()
    {
        when( dynamicSchemaService.createStyles( isA( CreateDynamicStylesParams.class ) ) ).thenAnswer( params -> {
            final CreateDynamicStylesParams stylesParams = params.getArgument( 0, CreateDynamicStylesParams.class );

            final XmlStyleDescriptorParser parser = new XmlStyleDescriptorParser();

            StyleDescriptor.Builder builder = StyleDescriptor.create();

            final Instant modifiedTime = Instant.parse( "2021-09-25T10:00:00.00Z" );
            builder.modifiedTime( modifiedTime );

            parser.styleDescriptorBuilder( builder );
            builder.application( stylesParams.getKey() );

            parser.source( stylesParams.getResource() );
            parser.currentApplication( stylesParams.getKey() );

            parser.parse();

            final Resource resource = mock( Resource.class );
            when( resource.readString() ).thenReturn( stylesParams.getResource() );

            return new DynamicSchemaResult<StyleDescriptor>( builder.build(), resource );
        } );

        runScript( "/lib/xp/examples/schema/createStyles.js" );
    }

    @Test
    void testInvalidStyles()
    {
        runFunction( "/test/CreateDynamicStylesHandlerTest.js", "createInvalidStyles" );
    }
}
