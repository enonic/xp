package com.enonic.xp.admin.impl.rest.resource.content;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.inputtype.InputTypeConfig;
import com.enonic.xp.inputtype.InputTypeProperty;
import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.content.ContentTypeService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ContentListTitleResolverTest
{
    @Mock
    ContentTypeService contentTypeService;

    @Test
    void resolve()
    {
        final ContentType contentType = ContentType.create()
            .name( "contentType" )
            .schemaConfig( InputTypeConfig.create()
                               .property(
                                   InputTypeProperty.create( "listTitleExpression", "${data.val1} ${missing} ${data.val1} ${displayName}" )
                                       .build() )
                               .build() )
            .superType( ContentTypeName.unstructured() )
            .build();

        final PropertyTree data = new PropertyTree();
        data.addString( "val1", "hello" );

        final Content content =
            Content.create().parentPath( ContentPath.ROOT ).name( "mycontent" ).data( data ).displayName( "my content" ).build();

        when( contentTypeService.getByName( any() ) ).thenReturn( contentType );
        final String result = new ContentListTitleResolver( contentTypeService ).resolve( content );
        assertEquals( "hello  hello my content", result );
    }

    @Test
    void resolve_no_list_title_expression_falls_back_to_displayName()
    {
        final ContentType contentType = ContentType.create().name( "contentType" ).superType( ContentTypeName.unstructured() ).build();

        final Content content = Content.create().parentPath( ContentPath.ROOT ).name( "mycontent" ).displayName( "my content" ).build();

        when( contentTypeService.getByName( any() ) ).thenReturn( contentType );
        final String result = new ContentListTitleResolver( contentTypeService ).resolve( content );
        assertEquals( "my content", result );
    }

    @Test
    void no_content_type_found_falls_back_to_displayName()
    {
        final Content content = Content.create().parentPath( ContentPath.ROOT ).name( "mycontent" ).displayName( "my content" ).build();

        when( contentTypeService.getByName( any() ) ).thenReturn( null );
        final String result = new ContentListTitleResolver( contentTypeService ).resolve( content );
        assertEquals( "my content", result );
    }

}
