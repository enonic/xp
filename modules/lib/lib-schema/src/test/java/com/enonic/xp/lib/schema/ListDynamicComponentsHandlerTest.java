package com.enonic.xp.lib.schema;

import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.form.Form;
import com.enonic.xp.form.Input;
import com.enonic.xp.inputtype.InputTypeName;
import com.enonic.xp.region.PartDescriptor;
import com.enonic.xp.resource.DynamicSchemaResult;
import com.enonic.xp.resource.ListDynamicComponentsParams;
import com.enonic.xp.resource.Resource;

import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ListDynamicComponentsHandlerTest
    extends BaseSchemaHandlerTest
{
    @Test
    void testParts()
    {
        when( dynamicSchemaService.listComponents( isA( ListDynamicComponentsParams.class ) ) ).thenAnswer( params -> {
            final ListDynamicComponentsParams componentsParams = params.getArgument( 0, ListDynamicComponentsParams.class );

            final Form partForm = Form.create()
                .addFormItem( Input.create().name( "width" ).label( "width" ).inputType( InputTypeName.DOUBLE ).build() )
                .build();

            final PartDescriptor partDescriptor = PartDescriptor.create()
                .displayName( "News part" )
                .config( partForm )
                .key( DescriptorKey.from( componentsParams.getKey(), "part1" ) )
                .modifiedTime( Instant.parse( "2021-02-25T10:44:33.170079900Z" ) )
                .description( "My news part" )
                .descriptionI18nKey( "key.description" )
                .build();

            final PartDescriptor otherPartDescriptor = PartDescriptor.create()
                .displayName( "Other part" )
                .config( Form.empty() )
                .key( DescriptorKey.from( componentsParams.getKey(), "part2" ) )
                .modifiedTime( Instant.parse( "2022-02-25T10:44:33.170079900Z" ) )
                .build();

            final Resource resource1 = mock( Resource.class );
            when( resource1.readString() ).thenReturn( "<part><some-data></some-data></part>" );

            final Resource resource2 = mock( Resource.class );
            when( resource2.readString() ).thenReturn( "<part><some-other-data></some-other-data></part>" );

            return List.of( new DynamicSchemaResult<PartDescriptor>( partDescriptor, resource1 ),
                            new DynamicSchemaResult<PartDescriptor>( otherPartDescriptor, resource2 ) );
        } );

        runScript( "/lib/xp/examples/schema/listParts.js" );
    }
}
