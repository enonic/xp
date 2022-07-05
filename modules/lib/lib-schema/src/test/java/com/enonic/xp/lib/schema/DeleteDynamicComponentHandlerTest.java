package com.enonic.xp.lib.schema;


import org.junit.jupiter.api.Test;

import com.enonic.xp.resource.DeleteDynamicComponentParams;
import com.enonic.xp.resource.DynamicComponentType;

import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;

public class DeleteDynamicComponentHandlerTest
    extends BaseSchemaHandlerTest
{
    @Test
    public void testPart()
    {
        when( dynamicSchemaService.deleteComponent( isA( DeleteDynamicComponentParams.class ) ) ).thenAnswer( params -> {
            final DeleteDynamicComponentParams componentParams = params.getArgument( 0, DeleteDynamicComponentParams.class );

            return DynamicComponentType.PART == componentParams.getType();
        } );

        runScript( "/lib/xp/examples/schema/deletePart.js" );
    }

    @Test
    public void testLayout()
    {
        when( dynamicSchemaService.deleteComponent( isA( DeleteDynamicComponentParams.class ) ) ).thenAnswer( params -> {
            final DeleteDynamicComponentParams componentParams = params.getArgument( 0, DeleteDynamicComponentParams.class );

            return DynamicComponentType.LAYOUT == componentParams.getType();
        } );

        runScript( "/lib/xp/examples/schema/deleteLayout.js" );
    }

    @Test
    public void testPage()
    {
        when( dynamicSchemaService.deleteComponent( isA( DeleteDynamicComponentParams.class ) ) ).thenAnswer( params -> {
            final DeleteDynamicComponentParams componentParams = params.getArgument( 0, DeleteDynamicComponentParams.class );

            return DynamicComponentType.PAGE == componentParams.getType();
        } );

        runScript( "/lib/xp/examples/schema/deletePage.js" );
    }


    @Test
    public void testInvalidSchemaType()
    {
        runFunction( "/test/DeleteDynamicComponentHandlerTest.js", "deleteInvalidComponentType" );
    }
}
