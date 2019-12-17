package com.enonic.xp.lib.content;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentEditor;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.EditableContent;
import com.enonic.xp.content.UpdateContentParams;
import com.enonic.xp.form.Form;
import com.enonic.xp.form.FormItemSet;
import com.enonic.xp.form.Input;
import com.enonic.xp.inputtype.InputTypeName;
import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.content.GetContentTypeParams;
import com.enonic.xp.schema.xdata.XData;
import com.enonic.xp.schema.xdata.XDataName;
import com.enonic.xp.site.SiteDescriptor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class ModifyContentHandlerTest
    extends BaseContentHandlerTest
{
    @Test
    public void testExample()
    {
        GetContentTypeParams getContentType = GetContentTypeParams.from( ContentTypeName.unstructured() );
        when( this.contentTypeService.getByName( getContentType ) ).thenReturn(
            ContentType.create().name( ContentTypeName.unstructured() ).setBuiltIn().build() );

        final Content content = TestDataFixtures.newExampleContent();
        when( this.contentService.getByPath( Mockito.any() ) ).thenReturn( content );

        when( this.contentService.update( Mockito.isA( UpdateContentParams.class ) ) ).thenAnswer(
            invocationOnMock -> invokeUpdate( (UpdateContentParams) invocationOnMock.getArguments()[0], content ) );

        mockXData();
        runScript( "/lib/xp/examples/content/modify.js" );
    }

    @Test
    public void modifySiteConfig()
    {
        final Content content = TestDataFixtures.newSmallContent();
        when( this.contentService.getByPath( content.getPath() ) ).thenReturn( content );

        when( this.contentService.update( Mockito.isA( UpdateContentParams.class ) ) ).thenAnswer(
            invocationOnMock -> invokeUpdate( (UpdateContentParams) invocationOnMock.getArguments()[0],
                                              TestDataFixtures.newSmallContent() ) );

        mockXData();

        final SiteDescriptor siteDescriptor1 = SiteDescriptor.create().
            form( Form.create().
                addFormItem( Input.create().
                    label( "a" ).
                    name( "a" ).
                    inputType( InputTypeName.TEXT_LINE ).
                    build() ).
                addFormItem( Input.create().
                    label( "b" ).
                    name( "b" ).
                    inputType( InputTypeName.CHECK_BOX ).
                    build() ).
                build() ).
            build();

        final SiteDescriptor siteDescriptor2 = SiteDescriptor.create().
            form( Form.create().
                addFormItem( Input.create().
                    label( "c" ).
                    name( "c" ).
                    inputType( InputTypeName.LONG ).
                    build() ).
                build() ).
            build();

        when( this.siteService.getDescriptor( ApplicationKey.from( "appKey1" ) ) ).thenReturn( siteDescriptor1 );
        when( this.siteService.getDescriptor( ApplicationKey.from( "appKey2" ) ) ).thenReturn( siteDescriptor2 );

        runFunction( "/test/ModifyContentHandlerTest.js", "modifySiteConfig" );
        runFunction( "/test/ModifyContentHandlerTest.js", "modifySiteConfig_strict" );
    }

    @Test
    public void modifySiteSingleDescriptor()
    {
        final Content content = TestDataFixtures.newSmallContent();
        when( this.contentService.getByPath( content.getPath() ) ).thenReturn( content );

        when( this.contentService.update( Mockito.isA( UpdateContentParams.class ) ) ).thenAnswer(
            invocationOnMock -> invokeUpdate( (UpdateContentParams) invocationOnMock.getArguments()[0],
                                              TestDataFixtures.newSmallContent() ) );

        mockXData();

        final SiteDescriptor siteDescriptor1 = SiteDescriptor.create().
            form( Form.create().
                addFormItem( Input.create().
                    label( "a" ).
                    name( "a" ).
                    inputType( InputTypeName.TEXT_LINE ).
                    build() ).
                addFormItem( Input.create().
                    label( "b" ).
                    name( "b" ).
                    inputType( InputTypeName.CHECK_BOX ).
                    build() ).
                build() ).
            build();

        when( this.siteService.getDescriptor( ApplicationKey.from( "appKey1" ) ) ).thenReturn( siteDescriptor1 );
        runFunction( "/test/ModifyContentHandlerTest.js", "modifySiteSingleDescriptor" );
    }


    @Test
    public void modifyById()
        throws Exception
    {
        when( this.contentService.update( Mockito.isA( UpdateContentParams.class ) ) ).thenAnswer(
            invocationOnMock -> invokeUpdate( (UpdateContentParams) invocationOnMock.getArguments()[0],
                                              TestDataFixtures.newSmallContent() ) );

        final Content content = TestDataFixtures.newSmallContent();
        when( this.contentService.getById( content.getId() ) ).thenReturn( content );

        mockXData();

        runFunction( "/test/ModifyContentHandlerTest.js", "modifyById" );
    }

    @Test
    public void modifyByPath()
        throws Exception
    {
        final Content content = TestDataFixtures.newSmallContent();
        when( this.contentService.getByPath( content.getPath() ) ).thenReturn( content );

        when( this.contentService.update( Mockito.isA( UpdateContentParams.class ) ) ).thenAnswer(
            invocationOnMock -> invokeUpdate( (UpdateContentParams) invocationOnMock.getArguments()[0],
                                              TestDataFixtures.newSmallContent() ) );

        mockXData();

        runFunction( "/test/ModifyContentHandlerTest.js", "modifyByPath" );
    }

    @Test
    public void modifyNotMappedXDataFieldNameStricted()
        throws Exception
    {
        final Content content = TestDataFixtures.newSmallContent();
        when( this.contentService.getByPath( content.getPath() ) ).thenReturn( content );

        when( this.contentService.update( Mockito.isA( UpdateContentParams.class ) ) ).thenAnswer(
            invocationOnMock -> invokeUpdate( (UpdateContentParams) invocationOnMock.getArguments()[0],
                                              TestDataFixtures.newSmallContent() ) );

        mockXData();

        runFunction( "/test/ModifyContentHandlerTest.js", "modifyNotMappedXDataFieldName_stricted" );
    }

    @Test
    public void modifyNotMappedXDataFieldNameNotStricted()
        throws Exception
    {
        final Content content = TestDataFixtures.newSmallContent();
        when( this.contentService.getByPath( content.getPath() ) ).thenReturn( content );

        when( this.contentService.update( Mockito.isA( UpdateContentParams.class ) ) ).thenAnswer(
            invocationOnMock -> invokeUpdate( (UpdateContentParams) invocationOnMock.getArguments()[0],
                                              TestDataFixtures.newSmallContent() ) );

        mockXData();

        runFunction( "/test/ModifyContentHandlerTest.js", "modifyNotMappedXDataFieldName_notStricted" );
    }

    @Test
    public void modifyNotFound()
        throws Exception
    {
        runFunction( "/test/ModifyContentHandlerTest.js", "modify_notFound" );
    }

    private void mockXData()
    {
        final FormItemSet cSet = FormItemSet.create().
            name( "c" ).
            occurrences( 0, 10 ).
            addFormItem( Input.create().
                label( "d" ).
                name( "d" ).
                inputType( InputTypeName.CHECK_BOX ).
                build() ).
            addFormItem( Input.create().
                label( "e" ).
                name( "e" ).
                occurrences( 0, 0 ).
                inputType( InputTypeName.TEXT_LINE ).
                build() ).
            addFormItem( Input.create().
                label( "f" ).
                name( "f" ).
                inputType( InputTypeName.LONG ).
                build() ).
            build();

        final ContentType contentType = ContentType.create().
            name( "test:myContentType" ).
            superType( ContentTypeName.structured() ).
            addFormItem( Input.create().
                label( "a" ).
                name( "a" ).
                inputType( InputTypeName.DOUBLE ).
                build() ).
            addFormItem( Input.create().
                label( "b" ).
                name( "b" ).
                inputType( InputTypeName.TEXT_LINE ).
                build() ).
            addFormItem( cSet ).
            addFormItem( Input.create().
                label( "z" ).
                name( "z" ).
                occurrences( 0, 10 ).
                inputType( InputTypeName.TEXT_LINE ).
                build() ).
            build();

        GetContentTypeParams getContentType = GetContentTypeParams.from( ContentTypeName.from( "test:myContentType" ) );
        when( this.contentTypeService.getByName( Mockito.eq( getContentType ) ) ).thenReturn( contentType );

        final XData xData1 = XData.create().
            name( XDataName.from( "com.enonic.myapplication:myschema" ) ).
            addFormItem( Input.create().
                label( "a" ).
                name( "a" ).
                inputType( InputTypeName.DOUBLE ).
                build() ).
            build();
        when( this.xDataService.getByName( Mockito.eq( xData1.getName() ) ) ).thenReturn( xData1 );

        final XData xData2 = XData.create().
            name( XDataName.from( "com.enonic.myapplication:other" ) ).
            addFormItem( Input.create().
                label( "name" ).
                name( "name" ).
                inputType( InputTypeName.TEXT_LINE ).
                build() ).
            build();
        when( this.xDataService.getByName( Mockito.eq( xData1.getName() ) ) ).thenReturn( xData1 );
        when( this.xDataService.getByName( Mockito.eq( xData2.getName() ) ) ).thenReturn( xData2 );
        when( this.mixinService.inlineFormItems( any( Form.class ) ) ).then( returnsFirstArg() );
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
