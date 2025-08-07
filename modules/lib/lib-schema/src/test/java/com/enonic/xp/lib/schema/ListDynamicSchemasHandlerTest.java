package com.enonic.xp.lib.schema;

import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.enonic.xp.form.FieldSet;
import com.enonic.xp.form.FormItemSet;
import com.enonic.xp.form.Input;
import com.enonic.xp.inputtype.InputTypeName;
import com.enonic.xp.resource.DynamicSchemaResult;
import com.enonic.xp.resource.ListDynamicContentSchemasParams;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.content.ContentTypeName;

import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ListDynamicSchemasHandlerTest
    extends BaseSchemaHandlerTest
{
    @Test
    public void listSchemas()
    {
        when( dynamicSchemaService.listContentSchemas( isA( ListDynamicContentSchemasParams.class ) ) ).thenAnswer( params -> {
            final ListDynamicContentSchemasParams schemasParams = params.getArgument( 0, ListDynamicContentSchemasParams.class );

            final ContentType contentType1 = ContentType.create()
                .superType( ContentTypeName.structured() )
                .description( "My type description" )
                .displayName( "My type display name" )
                .name( ContentTypeName.from( schemasParams.getKey() + ":type1" ) )
                .modifiedTime( Instant.parse( "2010-01-01T10:00:00Z" ) )
                .addFormItem( FieldSet.create()
                                  .label( "My layout" )
                                  .addFormItem( FormItemSet.create()
                                                    .name( "mySet" )
                                                    .required( true )
                                                    .addFormItem( Input.create()
                                                                      .name( "myInput" )
                                                                      .label( "Input" )
                                                                      .inputType( InputTypeName.TEXT_LINE )
                                                                      .build() )
                                                    .build() )
                                  .build() )
                .build();

            final ContentType contentType2 = ContentType.create()
                .superType( ContentTypeName.archiveMedia() )
                .description( "My type description 2" )
                .displayName( "My type display name 2" )
                .name( ContentTypeName.from( schemasParams.getKey() + ":type2" ) )
                .modifiedTime( Instant.parse( "2012-01-01T10:00:00Z" ) )
                .build();

            final Resource resource1 = mock( Resource.class );
            when( resource1.readString() ).thenReturn( "<content-type><some-data></some-data></content-type>" );

            final Resource resource2 = mock( Resource.class );
            when( resource2.readString() ).thenReturn( "<content-type><some-other-data></some-other-data></content-type>" );

            return List.of( new DynamicSchemaResult<ContentType>( contentType1, resource1 ),
                            new DynamicSchemaResult<ContentType>( contentType2, resource2 ) );
        } );

        runScript( "/lib/xp/examples/schema/listContentTypes.js" );
    }
}
