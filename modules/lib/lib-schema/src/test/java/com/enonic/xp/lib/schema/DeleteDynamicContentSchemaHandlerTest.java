package com.enonic.xp.lib.schema;


import org.junit.jupiter.api.Test;

import com.enonic.xp.resource.DeleteDynamicContentSchemaParams;
import com.enonic.xp.resource.DynamicContentSchemaType;

import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;

class DeleteDynamicContentSchemaHandlerTest
    extends BaseSchemaHandlerTest
{
    @Test
    void testContentType()
    {
        when( dynamicSchemaService.deleteContentSchema( isA( DeleteDynamicContentSchemaParams.class ) ) ).thenAnswer( params -> {
            final DeleteDynamicContentSchemaParams schemaParams = params.getArgument( 0, DeleteDynamicContentSchemaParams.class );

            return DynamicContentSchemaType.CONTENT_TYPE == schemaParams.getType();
        } );

        runScript( "/lib/xp/examples/schema/deleteContentType.js" );
    }

    @Test
    void testFormFragment()
    {
        when( dynamicSchemaService.deleteContentSchema( isA( DeleteDynamicContentSchemaParams.class ) ) ).thenAnswer( params -> {
            final DeleteDynamicContentSchemaParams schemaParams = params.getArgument( 0, DeleteDynamicContentSchemaParams.class );

            return DynamicContentSchemaType.FORM_FRAGMENT == schemaParams.getType();
        } );

        runScript( "/lib/xp/examples/schema/deleteFormFragment.js" );
    }

    @Test
    void testXData()
    {
        when( dynamicSchemaService.deleteContentSchema( isA( DeleteDynamicContentSchemaParams.class ) ) ).thenAnswer( params -> {
            final DeleteDynamicContentSchemaParams schemaParams = params.getArgument( 0, DeleteDynamicContentSchemaParams.class );

            return DynamicContentSchemaType.MIXIN == schemaParams.getType();
        } );

        runScript( "/lib/xp/examples/schema/deleteXData.js" );
    }


    @Test
    void testInvalidSchemaType()
    {
        runFunction( "/test/DeleteDynamicContentSchemaHandlerTest.js", "deleteInvalidContentSchemaType" );
    }
}
