package com.enonic.xp.lib.schema;


import org.junit.jupiter.api.Test;

import com.enonic.xp.resource.DeleteDynamicContentSchemaParams;
import com.enonic.xp.resource.DynamicContentSchemaType;

import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;

public class DeleteDynamicContentSchemaHandlerTest
    extends BaseSchemaHandlerTest
{
    @Test
    public void testContentType()
    {
        when( dynamicSchemaService.deleteContentSchema( isA( DeleteDynamicContentSchemaParams.class ) ) ).thenAnswer( params -> {
            final DeleteDynamicContentSchemaParams schemaParams = params.getArgument( 0, DeleteDynamicContentSchemaParams.class );

            return DynamicContentSchemaType.CONTENT_TYPE == schemaParams.getType();
        } );

        runScript( "/lib/xp/examples/schema/deleteContentType.js" );
    }

    @Test
    public void testMixin()
    {
        when( dynamicSchemaService.deleteContentSchema( isA( DeleteDynamicContentSchemaParams.class ) ) ).thenAnswer( params -> {
            final DeleteDynamicContentSchemaParams schemaParams = params.getArgument( 0, DeleteDynamicContentSchemaParams.class );

            return DynamicContentSchemaType.FORM_FRAGMENT == schemaParams.getType();
        } );

        runScript( "/lib/xp/examples/schema/deleteFormFragment.js" );
    }

    @Test
    public void testXData()
    {
        when( dynamicSchemaService.deleteContentSchema( isA( DeleteDynamicContentSchemaParams.class ) ) ).thenAnswer( params -> {
            final DeleteDynamicContentSchemaParams schemaParams = params.getArgument( 0, DeleteDynamicContentSchemaParams.class );

            return DynamicContentSchemaType.XDATA == schemaParams.getType();
        } );

        runScript( "/lib/xp/examples/schema/deleteXData.js" );
    }


    @Test
    public void testInvalidSchemaType()
    {
        runFunction( "/test/DeleteDynamicContentSchemaHandlerTest.js", "deleteInvalidContentSchemaType" );
    }
}
