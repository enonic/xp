package com.enonic.xp.lib.content;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import com.google.common.io.ByteSource;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.attachment.Attachment;
import com.enonic.xp.attachment.Attachments;
import com.enonic.xp.attachment.CreateAttachment;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentNotFoundException;
import com.enonic.xp.content.ContentPatcher;
import com.enonic.xp.content.PatchContentParams;
import com.enonic.xp.content.PatchContentResult;
import com.enonic.xp.content.PatchableContent;
import com.enonic.xp.form.Form;
import com.enonic.xp.form.FormItemSet;
import com.enonic.xp.form.Input;
import com.enonic.xp.inputtype.InputTypeName;
import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.content.GetContentTypeParams;
import com.enonic.xp.schema.mixin.MixinDescriptor;
import com.enonic.xp.schema.mixin.MixinName;
import com.enonic.xp.site.CmsDescriptor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class PatchContentHandlerTest
    extends BaseContentHandlerTest
{
    @Test
    void testExample()
    {
        final Content content = TestDataFixtures.newExampleContent();
        when( this.contentTypeService.getByName( isA( GetContentTypeParams.class ) ) ).thenReturn(
            ContentType.create().name( ContentTypeName.unstructured() ).setBuiltIn().build() );
        when( this.contentService.getByPath( Mockito.any() ) ).thenReturn( content );

        when( this.contentService.patch( Mockito.isA( PatchContentParams.class ) ) ).thenAnswer(
            invocationOnMock -> invokePatch( invocationOnMock.getArgument( 0 ), content ) );

        mockXData();
        runScript( "/lib/xp/examples/content/patch.js" );
    }

    @Test
    void patchSiteConfig()
    {
        final Content content = TestDataFixtures.newSmallContent();
        when( this.contentTypeService.getByName( isA( GetContentTypeParams.class ) ) ).thenReturn(
            ContentType.create().name( ContentTypeName.unstructured() ).setBuiltIn().build() );
        when( this.contentService.getByPath( content.getPath() ) ).thenReturn( content );

        when( this.contentService.patch( Mockito.isA( PatchContentParams.class ) ) ).thenAnswer(
            invocationOnMock -> invokePatch( invocationOnMock.getArgument( 0 ), TestDataFixtures.newSmallContent() ) );

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

        runFunction( "/test/PatchContentHandlerTest.js", "patchSiteConfig" );
    }

    @Test
    void patchSiteSingleDescriptor()
    {
        final Content content = TestDataFixtures.newSmallContent();
        when( this.contentTypeService.getByName( isA( GetContentTypeParams.class ) ) ).thenReturn(
            ContentType.create().name( ContentTypeName.unstructured() ).setBuiltIn().build() );
        when( this.contentService.getByPath( content.getPath() ) ).thenReturn( content );

        when( this.contentService.patch( Mockito.isA( PatchContentParams.class ) ) ).thenAnswer(
            invocationOnMock -> invokePatch( invocationOnMock.getArgument( 0 ), TestDataFixtures.newSmallContent() ) );

        mockXData();

        final CmsDescriptor siteDescriptor1 = CmsDescriptor.create()
            .applicationKey( ApplicationKey.from( "appKey1" ) )
            .form( Form.create()
                       .addFormItem( Input.create().label( "a" ).name( "a" ).inputType( InputTypeName.TEXT_LINE ).build() )
                       .addFormItem( Input.create().label( "b" ).name( "b" ).inputType( InputTypeName.CHECK_BOX ).build() )
                       .build() )
            .build();

        when( this.cmsService.getDescriptor( ApplicationKey.from( "appKey1" ) ) ).thenReturn( siteDescriptor1 );
        runFunction( "/test/PatchContentHandlerTest.js", "patchSiteSingleDescriptor" );
    }


    @Test
    void patchById()
    {
        when( this.contentService.patch( Mockito.isA( PatchContentParams.class ) ) ).thenAnswer(
            invocationOnMock -> invokePatch( invocationOnMock.getArgument( 0 ), TestDataFixtures.newSmallContent() ) );

        final Content content = TestDataFixtures.newSmallContent();
        when( this.contentTypeService.getByName( isA( GetContentTypeParams.class ) ) ).thenReturn(
            ContentType.create().name( ContentTypeName.unstructured() ).setBuiltIn().build() );
        when( this.contentService.getById( content.getId() ) ).thenReturn( content );

        mockXData();

        runFunction( "/test/PatchContentHandlerTest.js", "patchById" );
    }

    @Test
    void patchByPath()
    {
        final Content content = TestDataFixtures.newSmallContent();
        when( this.contentTypeService.getByName( isA( GetContentTypeParams.class ) ) ).thenReturn(
            ContentType.create().name( ContentTypeName.unstructured() ).setBuiltIn().build() );
        when( this.contentService.getByPath( content.getPath() ) ).thenReturn( content );

        when( this.contentService.patch( Mockito.isA( PatchContentParams.class ) ) ).thenAnswer(
            invocationOnMock -> invokePatch( invocationOnMock.getArgument( 0 ), TestDataFixtures.newSmallContent() ) );

        mockXData();

        runFunction( "/test/PatchContentHandlerTest.js", "patchByPath" );
    }

    @Test
    void patchNotMappedXDataFieldName()
    {
        final Content content = TestDataFixtures.newSmallContent();
        when( this.contentTypeService.getByName( isA( GetContentTypeParams.class ) ) ).thenReturn(
            ContentType.create().name( ContentTypeName.unstructured() ).setBuiltIn().build() );
        when( this.contentService.getByPath( content.getPath() ) ).thenReturn( content );

        when( this.contentService.patch( Mockito.isA( PatchContentParams.class ) ) ).thenAnswer(
            invocationOnMock -> invokePatch( invocationOnMock.getArgument( 0 ), TestDataFixtures.newSmallContent() ) );

        mockXData();

        runFunction( "/test/PatchContentHandlerTest.js", "patchNotMappedXDataFieldName" );
    }

    @Test
    void patchNotFound()
    {
        when( this.contentService.patch( Mockito.isA( PatchContentParams.class ) ) ).thenThrow( ContentNotFoundException.class );

        assertThrowsExactly( RuntimeException.class, () -> runFunction( "/test/PatchContentHandlerTest.js", "patch_notFound" ) );
    }

    @Test
    void patchWorkflowInfo()
    {
        final Content content = TestDataFixtures.newSmallContent();
        when( this.contentTypeService.getByName( isA( GetContentTypeParams.class ) ) ).thenReturn(
            ContentType.create().name( ContentTypeName.unstructured() ).setBuiltIn().build() );
        when( this.contentService.getByPath( content.getPath() ) ).thenReturn( content );

        when( this.contentService.patch( Mockito.isA( PatchContentParams.class ) ) ).thenAnswer(
            invocationOnMock -> invokePatch( invocationOnMock.getArgument( 0 ), TestDataFixtures.newSmallContent() ) );

        mockXData();

        runFunction( "/test/PatchContentHandlerTest.js", "patchWorkflowInfo" );
    }

    @Test
    void patchPageAllComponents()
    {
        final Content content = TestDataFixtures.newSmallContent();
        when( this.contentTypeService.getByName( isA( GetContentTypeParams.class ) ) ).thenReturn(
            ContentType.create().name( ContentTypeName.unstructured() ).setBuiltIn().build() );
        when( this.contentService.getByPath( content.getPath() ) ).thenReturn( content );

        when( this.contentService.patch( Mockito.isA( PatchContentParams.class ) ) ).thenAnswer(
            invocationOnMock -> invokePatch( invocationOnMock.getArgument( 0 ), TestDataFixtures.newSmallContent() ) );

        mockXData();

        runFunction( "/test/PatchContentHandlerTest.js", "patchPageAllComponents" );
    }

    @Test
    void patchValidationErrors()
    {
        final Content content = TestDataFixtures.newSmallContent();
        when( this.contentTypeService.getByName( isA( GetContentTypeParams.class ) ) ).thenReturn(
            ContentType.create().name( ContentTypeName.unstructured() ).setBuiltIn().build() );
        when( this.contentService.getByPath( content.getPath() ) ).thenReturn( content );

        when( this.contentService.patch( Mockito.isA( PatchContentParams.class ) ) ).thenAnswer(
            invocation -> invokePatch( invocation.getArgument( 0 ), content ) );

        mockXData();

        runFunction( "/test/PatchContentHandlerTest.js", "patchValidationErrors" );
    }

    @Test
    void patchWithSkipSync()
    {
        final Content content = TestDataFixtures.newSmallContent();
        when( this.contentTypeService.getByName( isA( GetContentTypeParams.class ) ) ).thenReturn(
            ContentType.create().name( ContentTypeName.unstructured() ).setBuiltIn().build() );
        when( this.contentService.getByPath( content.getPath() ) ).thenReturn( content );

        when( this.contentService.patch( Mockito.isA( PatchContentParams.class ) ) ).thenAnswer(
            invocationOnMock -> invokePatch( invocationOnMock.getArgument( 0 ), TestDataFixtures.newSmallContent() ) );

        mockXData();

        ArgumentCaptor<PatchContentParams> captor = ArgumentCaptor.forClass( PatchContentParams.class );

        runFunction( "/test/PatchContentHandlerTest.js", "patchWithSkipSync" );

        verify( this.contentService, times( 1 ) ).patch( captor.capture() );

        final PatchContentParams params = captor.getValue();

        assertTrue( params.isSkipSync() );
    }

    @Test
    void patchAttachments()
        throws Exception
    {
        final Content content = Content.create( TestDataFixtures.newSmallContent() )
            .attachments( Attachments.create()
                              .add( Attachment.create()
                                        .name( "file.txt" )
                                        .label( "initial label" )
                                        .mimeType( "image/jpeg" )
                                        .textContent( "initial content" )
                                        .sha512( "ABC" )
                                        .size( 123 )
                                        .build() )
                              .build() )
            .build();
        when( this.contentTypeService.getByName( isA( GetContentTypeParams.class ) ) ).thenReturn(
            ContentType.create().name( ContentTypeName.unstructured() ).setBuiltIn().build() );
        when( this.contentService.getByPath( content.getPath() ) ).thenReturn( content );

        when( this.contentService.patch( Mockito.isA( PatchContentParams.class ) ) ).thenAnswer(
            invocationOnMock -> invokePatch( invocationOnMock.getArgument( 0 ), content ) );

        mockXData();

        final ArgumentCaptor<PatchContentParams> captor = ArgumentCaptor.forClass( PatchContentParams.class );

        runFunction( "/test/PatchContentHandlerTest.js", "patchAttachments" );

        verify( this.contentService, times( 1 ) ).patch( captor.capture() );

        final PatchContentParams params = captor.getValue();

        final CreateAttachment createAttachment = params.getCreateAttachments().first();
        assertEquals( "file4.txt", createAttachment.getName() );
        assertEquals( "File 4", createAttachment.getLabel() );
        assertEquals( "text/plain", createAttachment.getMimeType() );
        assertEquals( "data 4", createAttachment.getTextContent() );
        assertEquals( "data 2", new String( createAttachment.getByteSource().read() ) );

        final PatchableContent patchable = new PatchableContent( content );
        params.getPatcher().patch( patchable );

        final Attachment patchedAttachment = patchable.build().getAttachments().first();

        assertEquals( "file.txt", patchedAttachment.getName() );
        assertEquals( "File 1", patchedAttachment.getLabel() );
        assertEquals( "text/plain", patchedAttachment.getMimeType() );
        assertEquals( "2c26b46b68ffc68ff99b453c1d30413413422d706483bfa0f98a5e886266e7ae", patchedAttachment.getSha512() );
        assertEquals( 14, patchedAttachment.getSize() );
        assertEquals( "data 1", patchedAttachment.getTextContent() );
    }

    @Test
    void patchAttachmentsNonExistingAttachment()
    {
        final Content contentWithoutAttachments = TestDataFixtures.newSmallContent();
        when( this.contentTypeService.getByName( isA( GetContentTypeParams.class ) ) ).thenReturn(
            ContentType.create().name( ContentTypeName.unstructured() ).setBuiltIn().build() );
        when( this.contentService.getByPath( contentWithoutAttachments.getPath() ) ).thenReturn( contentWithoutAttachments );

        when( this.contentService.patch( Mockito.isA( PatchContentParams.class ) ) ).thenAnswer(
            invocationOnMock -> invokePatch( invocationOnMock.getArgument( 0 ), contentWithoutAttachments ) );

        mockXData();

        assertThrowsExactly( RuntimeException.class, () -> runFunction( "/test/PatchContentHandlerTest.js", "patchAttachments" ) );
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

        GetContentTypeParams getContentType = GetContentTypeParams.from( ContentTypeName.from( "test:myContentType" ) );
        when( this.contentTypeService.getByName( eq( getContentType ) ) ).thenReturn( contentType );

        final MixinDescriptor descriptor1 = MixinDescriptor.create()
            .name( MixinName.from( "com.enonic.myapplication:myschema" ) )
            .addFormItem( Input.create().label( "a" ).name( "a" ).inputType( InputTypeName.DOUBLE ).build() )
            .build();
        when( this.mixinService.getByName( eq( descriptor1.getName() ) ) ).thenReturn( descriptor1 );

        final MixinDescriptor descriptor2 = MixinDescriptor.create()
            .name( MixinName.from( "com.enonic.myapplication:other" ) )
            .addFormItem( Input.create().label( "name" ).name( "name" ).inputType( InputTypeName.TEXT_LINE ).build() )
            .build();
        when( this.mixinService.getByName( eq( descriptor1.getName() ) ) ).thenReturn( descriptor1 );
        when( this.mixinService.getByName( eq( descriptor2.getName() ) ) ).thenReturn( descriptor2 );
        when( this.formFragmentService.inlineFormItems( any( Form.class ) ) ).then( returnsFirstArg() );
    }

    private PatchContentResult invokePatch( final PatchContentParams params, final Content content )
    {
        assertEquals( ContentId.from( "123456" ), params.getContentId() );

        final ContentPatcher patcher = params.getPatcher();
        assertNotNull( patcher );

        final PatchableContent patchable = new PatchableContent( content );

        patcher.patch( patchable );

        return PatchContentResult.create()
            .contentId( params.getContentId() )
            .addResult( ContentConstants.BRANCH_DRAFT, patchable.build() )
            .build();
    }

    // used from js tests
    @SuppressWarnings("unused")
    public static ByteSource createByteSource( final String value )
    {
        return ByteSource.wrap( value.getBytes() );
    }
}
