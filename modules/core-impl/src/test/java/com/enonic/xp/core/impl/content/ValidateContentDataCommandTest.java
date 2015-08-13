package com.enonic.xp.core.impl.content;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.content.Content;
import com.enonic.xp.form.FieldSet;
import com.enonic.xp.form.FormItemSet;
import com.enonic.xp.form.Input;
import com.enonic.xp.form.inputtype.InputTypeName;
import com.enonic.xp.form.inputtype.InputTypes2;
import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.content.ContentTypeService;
import com.enonic.xp.schema.content.GetContentTypeParams;
import com.enonic.xp.schema.content.validator.DataValidationErrors;
import com.enonic.xp.schema.mixin.MixinService;
import com.enonic.xp.site.SiteService;

import static org.junit.Assert.*;

public class ValidateContentDataCommandTest
{
    private ContentTypeService contentTypeService;

    private MixinService mixinService;

    private SiteService siteService;

    @Before
    public void setUp()
        throws Exception
    {
        this.contentTypeService = Mockito.mock( ContentTypeService.class );
        this.mixinService = Mockito.mock( MixinService.class );
        this.siteService = Mockito.mock( SiteService.class );
    }

    @Test
    public void validation_with_errors()
        throws Exception
    {
        // setup
        final ContentType contentType = ContentType.create().
            superType( ContentTypeName.structured() ).
            name( "myapplication:my_type" ).
            addFormItem( FieldSet.create().
                label( "My layout" ).
                name( "myLayout" ).
                addFormItem( FormItemSet.create().name( "mySet" ).required( true ).
                    addFormItem( Input.create().name( "myInput" ).label( "Input" ).inputType( InputTypeName.TEXT_LINE ).
                        build() ).
                    build() ).
                build() ).
            build();

        Mockito.when( contentTypeService.getByName( Mockito.isA( GetContentTypeParams.class ) ) ).thenReturn( contentType );

        final Content content = Content.create().path( "/mycontent" ).type( contentType.getName() ).build();

        // exercise

        final DataValidationErrors result = ValidateContentDataCommand.create().
            contentData( content.getData() ).
            contentType( contentType.getName() ).
            contentTypeService( this.contentTypeService ).
            mixinService( this.mixinService ).
            siteService( this.siteService ).
            inputTypeResolver( InputTypes2.BUILTIN ).
            build().
            execute();

        // test
        assertTrue( result.hasErrors() );
        assertEquals( 1, result.size() );

    }

    @Test
    public void validation_no_errors()
        throws Exception
    {
        // setup
        final FieldSet fieldSet = FieldSet.create().label( "My layout" ).name( "myLayout" ).addFormItem(
            FormItemSet.create().name( "mySet" ).required( true ).addFormItem(
                Input.create().name( "myInput" ).label( "Input" ).inputType( InputTypeName.TEXT_LINE ).build() ).build() ).build();
        final ContentType contentType = ContentType.create().
            superType( ContentTypeName.structured() ).
            name( "myapplication:my_type" ).
            addFormItem( fieldSet ).
            build();

        Mockito.when( contentTypeService.getByName( Mockito.isA( GetContentTypeParams.class ) ) ).thenReturn( contentType );

        final Content content = Content.create().path( "/mycontent" ).type( contentType.getName() ).build();
        content.getData().setString( "mySet.myInput", "thing" );

        // exercise
        final DataValidationErrors result = ValidateContentDataCommand.create().
            contentData( content.getData() ).
            contentType( contentType.getName() ).
            contentTypeService( this.contentTypeService ).
            mixinService( this.mixinService ).
            siteService( this.siteService ).
            inputTypeResolver( InputTypes2.BUILTIN ).
            build().
            execute();

        // test

        assertFalse( result.hasErrors() );
        assertEquals( 0, result.size() );
    }

}
