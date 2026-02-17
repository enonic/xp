package com.enonic.xp.lib.content;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentEditor;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentNotFoundException;
import com.enonic.xp.content.EditableContent;
import com.enonic.xp.content.UpdateContentParams;
import com.enonic.xp.form.Form;
import com.enonic.xp.form.FormItemSet;
import com.enonic.xp.form.Input;
import com.enonic.xp.inputtype.InputTypeName;
import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.mixin.MixinDescriptor;
import com.enonic.xp.schema.mixin.MixinName;
import com.enonic.xp.site.CmsDescriptor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

class UpdateContentHandlerTest
    extends BaseContentHandlerTest
{
    @Test
    void testExample()
    {
        when( this.contentTypeService.getByName( any() ) ).thenReturn(
            ContentType.create().name( ContentTypeName.unstructured() ).setBuiltIn().build() );

        final Content content = TestDataFixtures.newExampleContent();
        when( this.contentService.getByPath( Mockito.any() ) ).thenReturn( content );

        when( this.contentService.update( Mockito.isA( UpdateContentParams.class ) ) ).thenAnswer(
            invocationOnMock -> invokeUpdate( invocationOnMock.getArgument( 0 ), content ) );

        mockXData();
        runScript( "/lib/xp/examples/content/update.js" );
    }

    @Test
    void updateSiteConfig()
    {
        final Content content = TestDataFixtures.newSmallContent();
        when( this.contentService.getByPath( content.getPath() ) ).thenReturn( content );

        when( this.contentService.update( Mockito.isA( UpdateContentParams.class ) ) ).thenAnswer(
            invocationOnMock -> invokeUpdate( invocationOnMock.getArgument( 0 ), TestDataFixtures.newSmallContent() ) );

        mockXData();

        final CmsDescriptor siteDescriptor1 = CmsDescriptor.create()
            .applicationKey( ApplicationKey.from( "appKey1" ) )
            .form( Form.create()
                       .addFormItem( Input.create().label( "a" ).name( "a" ).inputType( InputTypeName.TEXT_LINE ).build() )
                       .addFormItem( Input.create().label( "b" ).name( "b" ).inputType( InputTypeName.CHECK_BOX ).build() )
                       .build() )
            .build();

        final CmsDescriptor siteDescriptor2 = CmsDescriptor.create()
            .applicationKey( ApplicationKey.from( "appKey2" ) )
            .form( Form.create().addFormItem( Input.create().label( "c" ).name( "c" ).inputType( InputTypeName.LONG ).build() ).build() )
            .build();

        when( this.cmsService.getDescriptor( ApplicationKey.from( "appKey1" ) ) ).thenReturn( siteDescriptor1 );
        when( this.cmsService.getDescriptor( ApplicationKey.from( "appKey2" ) ) ).thenReturn( siteDescriptor2 );

        runFunction( "/test/UpdateContentHandlerTest.js", "updateSiteConfig" );
        runFunction( "/test/UpdateContentHandlerTest.js", "updateSiteConfig_strict" );
    }

    @Test
    void updateSiteSingleDescriptor()
    {
        final Content content = TestDataFixtures.newSmallContent();
        when( this.contentService.getByPath( content.getPath() ) ).thenReturn( content );

        when( this.contentService.update( Mockito.isA( UpdateContentParams.class ) ) ).thenAnswer(
            invocationOnMock -> invokeUpdate( invocationOnMock.getArgument( 0 ), TestDataFixtures.newSmallContent() ) );

        mockXData();

        final CmsDescriptor siteDescriptor1 = CmsDescriptor.create().applicationKey( ApplicationKey.from( "appKey1" ) )
            .form( Form.create()
                       .addFormItem( Input.create().label( "a" ).name( "a" ).inputType( InputTypeName.TEXT_LINE ).build() )
                       .addFormItem( Input.create().label( "b" ).name( "b" ).inputType( InputTypeName.CHECK_BOX ).build() )
                       .build() )
            .build();

        when( this.cmsService.getDescriptor( ApplicationKey.from( "appKey1" ) ) ).thenReturn( siteDescriptor1 );
        runFunction( "/test/UpdateContentHandlerTest.js", "updateSiteSingleDescriptor" );
    }


    @Test
    void updateById()
    {
        when( this.contentService.update( Mockito.isA( UpdateContentParams.class ) ) ).thenAnswer(
            invocationOnMock -> invokeUpdate( invocationOnMock.getArgument( 0 ), TestDataFixtures.newSmallContent() ) );

        final Content content = TestDataFixtures.newSmallContent();
        when( this.contentService.getById( content.getId() ) ).thenReturn( content );

        mockXData();

        runFunction( "/test/UpdateContentHandlerTest.js", "updateById" );
    }

    @Test
    void updateByPath()
    {
        final Content content = TestDataFixtures.newSmallContent();
        when( this.contentService.getByPath( content.getPath() ) ).thenReturn( content );

        when( this.contentService.update( Mockito.isA( UpdateContentParams.class ) ) ).thenAnswer(
            invocationOnMock -> invokeUpdate( invocationOnMock.getArgument( 0 ), TestDataFixtures.newSmallContent() ) );

        mockXData();

        runFunction( "/test/UpdateContentHandlerTest.js", "updateByPath" );
    }

    @Test
    void updateNotMappedXDataFieldNameStricted()
    {
        final Content content = TestDataFixtures.newSmallContent();
        when( this.contentService.getByPath( content.getPath() ) ).thenReturn( content );

        when( this.contentService.update( Mockito.isA( UpdateContentParams.class ) ) ).thenAnswer(
            invocationOnMock -> invokeUpdate( invocationOnMock.getArgument( 0 ), TestDataFixtures.newSmallContent() ) );

        mockXData();

        runFunction( "/test/UpdateContentHandlerTest.js", "updateNotMappedXDataFieldName_stricted" );
    }

    @Test
    void updateNotMappedXDataFieldNameNotStricted()
    {
        final Content content = TestDataFixtures.newSmallContent();
        when( this.contentService.getByPath( content.getPath() ) ).thenReturn( content );

        when( this.contentService.update( Mockito.isA( UpdateContentParams.class ) ) ).thenAnswer(
            invocationOnMock -> invokeUpdate( invocationOnMock.getArgument( 0 ), TestDataFixtures.newSmallContent() ) );

        mockXData();

        runFunction( "/test/UpdateContentHandlerTest.js", "updateNotMappedXDataFieldName_notStricted" );
    }

    @Test
    void updateNotFound()
    {
        when( this.contentService.update( Mockito.isA( UpdateContentParams.class ) ) ).thenThrow( ContentNotFoundException.class );

        assertThrows( ContentNotFoundException.class, () -> runFunction( "/test/UpdateContentHandlerTest.js", "update_notFound" ) );
    }

    @Test
    void updatePageAllComponents()
    {
        final Content content = TestDataFixtures.newSmallContent();
        when( this.contentService.getByPath( content.getPath() ) ).thenReturn( content );

        when( this.contentService.update( Mockito.isA( UpdateContentParams.class ) ) ).thenAnswer(
            invocationOnMock -> invokeUpdate( invocationOnMock.getArgument( 0 ), TestDataFixtures.newSmallContent() ) );

        mockXData();

        runFunction( "/test/UpdateContentHandlerTest.js", "updatePageAllComponents" );
    }

    private void mockXData()
    {
        final FormItemSet cSet = FormItemSet.create()
            .name( "c" )
            .occurrences( 0, 10 )
            .addFormItem( Input.create().label( "d" ).name( "d" ).inputType( InputTypeName.CHECK_BOX ).build() )
            .addFormItem( Input.create().label( "e" ).name( "e" ).occurrences( 0, 0 ).inputType( InputTypeName.TEXT_LINE ).build() )
            .addFormItem( Input.create().label( "f" ).name( "f" ).inputType( InputTypeName.LONG ).build() )
            .build();

        final ContentType contentType = ContentType.create()
            .name( "test:myContentType" )
            .superType( ContentTypeName.structured() )
            .addFormItem( Input.create().label( "a" ).name( "a" ).inputType( InputTypeName.DOUBLE ).build() )
            .addFormItem( Input.create().label( "b" ).name( "b" ).inputType( InputTypeName.TEXT_LINE ).build() )
            .addFormItem( cSet )
            .addFormItem( Input.create().label( "z" ).name( "z" ).occurrences( 0, 10 ).inputType( InputTypeName.TEXT_LINE ).build() )
            .build();

        when( this.contentTypeService.getByName(
            argThat( argument -> argument.getContentTypeName().equals( ContentTypeName.from( "test:myContentType" ) ) ) ) ).thenReturn(
            contentType );

        final MixinDescriptor mixinDescriptor1 = MixinDescriptor.create()
            .name( MixinName.from( "com.enonic.myapplication:myschema" ) )
            .addFormItem( Input.create().label( "a" ).name( "a" ).inputType( InputTypeName.DOUBLE ).build() )
            .build();
        when( this.mixinService.getByName( eq( mixinDescriptor1.getName() ) ) ).thenReturn( mixinDescriptor1 );

        final MixinDescriptor mixinDescriptor2 = MixinDescriptor.create()
            .name( MixinName.from( "com.enonic.myapplication:other" ) )
            .addFormItem( Input.create().label( "name" ).name( "name" ).inputType( InputTypeName.TEXT_LINE ).build() )
            .build();
        when( this.mixinService.getByName( eq( mixinDescriptor1.getName() ) ) ).thenReturn( mixinDescriptor1 );
        when( this.mixinService.getByName( eq( mixinDescriptor2.getName() ) ) ).thenReturn( mixinDescriptor2 );
        when( formFragmentService.inlineFormItems( any() ) ).thenAnswer( returnsFirstArg() );
    }

    private Content invokeUpdate( final UpdateContentParams params, final Content content )
    {
        assertEquals( ContentId.from( "123456" ), params.getContentId() );

        final ContentEditor editor = params.getEditor();
        assertNotNull( editor );

        final EditableContent editable = new EditableContent( content );

        editor.edit( editable );
        return editable.build();
    }
}
