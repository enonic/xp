package com.enonic.xp.lib.content;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentEditor;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.EditableContent;
import com.enonic.xp.content.UpdateContentParams;
import com.enonic.xp.form.FormItemSet;
import com.enonic.xp.form.Input;
import com.enonic.xp.form.inputtype.InputTypes;
import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.content.GetContentTypeParams;
import com.enonic.xp.schema.mixin.Mixin;

public class ModifyContentHandlerTest
    extends BaseContentHandlerTest
{

    @Test
    public void modifyById()
        throws Exception
    {
        Mockito.when( this.contentService.update( Mockito.isA( UpdateContentParams.class ) ) ).thenAnswer(
            invocationOnMock -> invokeUpdate( (UpdateContentParams) invocationOnMock.getArguments()[0] ) );

        final Content content = TestDataFixtures.newSmallContent();
        Mockito.when( this.contentService.getById( content.getId() ) ).thenReturn( content );

        mockXData();

        runTestFunction( "/test/ModifyContentHandlerTest.js", "modifyById" );
    }

    @Test
    public void modifyByPath()
        throws Exception
    {
        final Content content = TestDataFixtures.newSmallContent();
        Mockito.when( this.contentService.getByPath( content.getPath() ) ).thenReturn( content );

        Mockito.when( this.contentService.update( Mockito.isA( UpdateContentParams.class ) ) ).thenAnswer(
            invocationOnMock -> invokeUpdate( (UpdateContentParams) invocationOnMock.getArguments()[0] ) );

        mockXData();

        runTestFunction( "/test/ModifyContentHandlerTest.js", "modifyByPath" );
    }

    @Test
    public void modifyNotFound()
        throws Exception
    {
        runTestFunction( "/test/ModifyContentHandlerTest.js", "modify_notFound" );
    }

    private void mockXData()
    {
        final FormItemSet cSet = FormItemSet.create().
            name( "c" ).
            occurrences( 0, 10 ).
            addFormItem( Input.create().
                label( "d" ).
                name( "d" ).
                inputType( InputTypes.CHECKBOX ).
                build() ).
            addFormItem( Input.create().
                label( "e" ).
                name( "e" ).
                occurrences( 0, 0 ).
                inputType( InputTypes.TEXT_LINE ).
                build() ).
            addFormItem( Input.create().
                label( "f" ).
                name( "f" ).
                inputType( InputTypes.LONG ).
                build() ).
            build();

        final ContentType contentType = ContentType.create().
            name( "test:myContentType" ).
            superType( ContentTypeName.structured() ).
            addFormItem( Input.create().
                label( "a" ).
                name( "a" ).
                inputType( InputTypes.DOUBLE ).
                build() ).
            addFormItem( Input.create().
                label( "b" ).
                name( "b" ).
                inputType( InputTypes.TEXT_LINE ).
                build() ).
            addFormItem( cSet ).
            addFormItem( Input.create().
                label( "z" ).
                name( "z" ).
                occurrences( 0, 10 ).
                inputType( InputTypes.TEXT_LINE ).
                build() ).
            build();

        GetContentTypeParams getContentType = GetContentTypeParams.from( ContentTypeName.from( "test:myContentType" ) );
        Mockito.when( this.contentTypeService.getByName( Mockito.eq( getContentType ) ) ).thenReturn( contentType );

        final Mixin mixin1 = Mixin.create().
            name( "com.enonic.mymodule:myschema" ).
            addFormItem( Input.create().
                label( "a" ).
                name( "a" ).
                inputType( InputTypes.DOUBLE ).
                build() ).
            build();
        Mockito.when( this.mixinService.getByName( Mockito.eq( mixin1.getName() ) ) ).thenReturn( mixin1 );

        final Mixin mixin2 = Mixin.create().
            name( "com.enonic.mymodule:other" ).
            addFormItem( Input.create().
                label( "name" ).
                name( "name" ).
                inputType( InputTypes.TEXT_LINE ).
                build() ).
            build();
        Mockito.when( this.mixinService.getByName( Mockito.eq( mixin2.getName() ) ) ).thenReturn( mixin2 );
    }

    private Content invokeUpdate( final UpdateContentParams params )
    {
        Assert.assertEquals( ContentId.from( "123456" ), params.getContentId() );

        final ContentEditor editor = params.getEditor();
        Assert.assertNotNull( editor );

        final Content content = TestDataFixtures.newSmallContent();
        final EditableContent editable = new EditableContent( content );

        editor.edit( editable );
        return editable.build();
    }

}
