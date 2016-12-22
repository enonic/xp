package com.enonic.xp.core.content;

import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentIds;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.CreateContentParams;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.form.Input;
import com.enonic.xp.inputtype.InputTypeName;
import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.content.ContentTypeService;
import com.enonic.xp.schema.content.GetContentTypeParams;

import static org.junit.Assert.*;

public class ContentServiceImplTest_isValidContent
    extends AbstractContentServiceTest
{
    @Override
    public void setUp()
        throws Exception
    {
        super.setUp();
    }

    @Test
    public void test_simple_content_is_valid()
        throws Exception
    {
        final Content content = this.contentService.create( CreateContentParams.create().
            contentData( new PropertyTree() ).
            displayName( "This is my content" ).
            parent( ContentPath.ROOT ).
            type( ContentTypeName.folder() ).
            build() );

        assertTrue( this.contentService.isValidContent( ContentIds.from( content.getId() ) ) );
    }

    @Test
    public void test_invalid_content()
        throws Exception
    {
        final ContentTypeService contentTypeService = Mockito.mock( ContentTypeService.class );
        this.contentService.setContentTypeService( contentTypeService );
        Mockito.when( contentTypeService.getByName( Mockito.isA( GetContentTypeParams.class ) ) ).
            thenReturn( ContentType.create().
                superType( ContentTypeName.documentMedia() ).
                name( "myContentType" ).
                addFormItem( Input.create().
                    label( "double" ).
                    name( "Double" ).
                    required( true ).
                    inputType( InputTypeName.DOUBLE ).
                    build() ).build() );

        PropertyTree data = new PropertyTree();

        final Content content = this.contentService.create( CreateContentParams.create().
            type( ContentTypeName.from( "myContentType" ) ).
            contentData( data ).
            requireValid( false ).
            parent( ContentPath.ROOT ).
            displayName( "my display-name" ).
            build() );

        assertFalse( this.contentService.isValidContent( ContentIds.from( content.getId() ) ) );
    }

    @Test
    public void test_valid_and_invalid_contents_return_false()
        throws Exception
    {
        final ContentTypeService contentTypeService = Mockito.mock( ContentTypeService.class );
        this.contentService.setContentTypeService( contentTypeService );
        Mockito.when( contentTypeService.getByName( Mockito.isA( GetContentTypeParams.class ) ) ).
            thenReturn( ContentType.create().
                superType( ContentTypeName.documentMedia() ).
                name( "myContentType" ).
                addFormItem( Input.create().
                    label( "double" ).
                    name( "Double" ).
                    required( true ).
                    inputType( InputTypeName.DOUBLE ).
                    build() ).build() );

        PropertyTree data = new PropertyTree();

        final Content content = this.contentService.create( CreateContentParams.create().
            type( ContentTypeName.from( "myContentType" ) ).
            contentData( data ).
            requireValid( false ).
            parent( ContentPath.ROOT ).
            displayName( "my display-name" ).
            build() );

        final Content content2 = this.contentService.create( CreateContentParams.create().
            contentData( new PropertyTree() ).
            displayName( "This is my content" ).
            parent( ContentPath.ROOT ).
            type( ContentTypeName.folder() ).
            build() );

        assertFalse( this.contentService.isValidContent( ContentIds.from( content.getId(), content2.getId() ) ) );
    }
}
