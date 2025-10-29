package com.enonic.xp.core.impl.content.serializer;

import java.util.Map;

import org.junit.jupiter.api.Test;

import com.google.common.io.ByteSource;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.attachment.Attachment;
import com.enonic.xp.attachment.Attachments;
import com.enonic.xp.attachment.CreateAttachment;
import com.enonic.xp.attachment.CreateAttachments;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentPropertyNames;
import com.enonic.xp.content.Mixin;
import com.enonic.xp.content.Mixins;
import com.enonic.xp.content.ValidationError;
import com.enonic.xp.content.ValidationErrorCode;
import com.enonic.xp.content.ValidationErrors;
import com.enonic.xp.content.WorkflowCheckState;
import com.enonic.xp.content.WorkflowInfo;
import com.enonic.xp.content.WorkflowState;
import com.enonic.xp.core.impl.content.CreateContentTranslatorParams;
import com.enonic.xp.data.PropertyPath;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.data.ValueFactory;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.form.Form;
import com.enonic.xp.form.Input;
import com.enonic.xp.index.ChildOrder;
import com.enonic.xp.inputtype.InputTypeName;
import com.enonic.xp.page.Page;
import com.enonic.xp.page.PageDescriptor;
import com.enonic.xp.region.RegionDescriptors;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.mixin.MixinName;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.util.BinaryReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ContentDataSerializerTest
{
    @Test
    void create_propertyTree_populated_with_attachment_properties()
    {
        final ContentDataSerializer contentDataSerializer = new ContentDataSerializer();

        final String binaryName = "myName";
        final String binaryLabel = "myLabel";
        final String binaryMimeType = "myMimeType";
        final byte[] binaryData = "my binary".getBytes();

        final CreateContentTranslatorParams params = CreateContentTranslatorParams.create()
            .parent( ContentPath.ROOT )
            .name( "myContentName" )
            .contentData( new PropertyTree() )
            .displayName( "myDisplayName" )
            .type( ContentTypeName.codeMedia() )
            .creator( PrincipalKey.ofAnonymous() )
            .childOrder( ChildOrder.defaultOrder() )
            .createAttachments( CreateAttachments.from( CreateAttachment.create()
                                                            .byteSource( ByteSource.wrap( binaryData ) )
                                                            .label( binaryLabel )
                                                            .mimeType( binaryMimeType )
                                                            .name( binaryName )
                                                            .build() ) )
            .build();

        final PropertyTree data = contentDataSerializer.toCreateNodeData( params );

        final PropertySet attachmentData = data.getSet( ContentPropertyNames.ATTACHMENT );
        assertNotNull( attachmentData );
        assertEquals( binaryName, attachmentData.getString( ContentPropertyNames.ATTACHMENT_NAME ) );
        assertEquals( binaryLabel, attachmentData.getString( ContentPropertyNames.ATTACHMENT_LABEL ) );
        assertEquals( binaryMimeType, attachmentData.getString( ContentPropertyNames.ATTACHMENT_MIMETYPE ) );
        assertEquals( binaryData.length + "", attachmentData.getString( ContentPropertyNames.ATTACHMENT_SIZE ) );
        assertEquals( binaryName, attachmentData.getString( ContentPropertyNames.ATTACHMENT_BINARY_REF ) );
    }

    @Test
    void update_propertyTree_populated_with_new_attachment_properties()
    {
        final ContentDataSerializer contentDataSerializer = new ContentDataSerializer();

        final String binaryName = "myName";
        final String binaryLabel = "myLabel";
        final String binaryMimeType = "myMimeType";

        final PropertyTree tree = new PropertyTree();
        tree.addProperty( "htmlData", ValueFactory.newString( "<img src =\"source\" data-src=\"image://image-id\" src=\"image/123\"/>" ) );

        final Form form = Form.create()
            .addFormItem( Input.create().name( "htmlData" ).label( "htmlData" ).inputType( InputTypeName.HTML_AREA ).build() )
            .build();

        final PageDescriptor pageDescriptor = PageDescriptor.create()
            .config( form )
            .regions( RegionDescriptors.create().build() )
            .key( DescriptorKey.from( "aaa:bbb" ) )
            .build();

        final Page page = Page.create().config( tree ).descriptor( pageDescriptor.getKey() ).build();

        final Content content = Content.create()
            .name( "myContent" )
            .parentPath( ContentPath.ROOT )
            .creator( PrincipalKey.ofAnonymous() )
            .modifier( PrincipalKey.ofAnonymous() ).page( page )
            .attachments( Attachments.from(
                Attachment.create().label( binaryLabel ).size( 11 ).mimeType( binaryMimeType ).name( binaryName ).build() ) )
            .validationErrors( ValidationErrors.create()
                                   .add( ValidationError.attachmentError( ValidationErrorCode.from( ApplicationKey.SYSTEM, "SOME_CODE" ),
                                                                          BinaryReference.from( "prevFile" ) )
                                             .message( "someError" )
                                             .build() )
                                   .build() )
            .build();
        final PropertyTree data = contentDataSerializer.toNodeData( content );

        final PropertySet attachmentData = data.getSet( ContentPropertyNames.ATTACHMENT );
        assertNotNull( attachmentData );
        assertEquals( binaryName, attachmentData.getString( ContentPropertyNames.ATTACHMENT_NAME ) );
        assertEquals( binaryLabel, attachmentData.getString( ContentPropertyNames.ATTACHMENT_LABEL ) );
        assertEquals( binaryMimeType, attachmentData.getString( ContentPropertyNames.ATTACHMENT_MIMETYPE ) );
        assertEquals( "11", attachmentData.getString( ContentPropertyNames.ATTACHMENT_SIZE ) );
        assertEquals( binaryName, attachmentData.getString( ContentPropertyNames.ATTACHMENT_BINARY_REF ) );
    }


    @Test
    void update_validationErrors()
    {
        final ContentDataSerializer contentDataSerializer = new ContentDataSerializer();

        final ValidationErrors validationErrors = ValidationErrors.create()
            .add( ValidationError.attachmentError( ValidationErrorCode.from( ApplicationKey.SYSTEM, "SOME_CODE" ),
                                                   BinaryReference.from( "myName" ) ).message( "someError" ).build() )
            .add( ValidationError.dataError( ValidationErrorCode.from( ApplicationKey.SYSTEM, "SOME_OTHER_CODE" ), PropertyPath.from( "" ) )
                      .message( "someDataError" )
                      .build() )
            .add( ValidationError.generalError( ValidationErrorCode.from( ApplicationKey.SYSTEM, "SERIOUS_ERROR" ) )
                      .message( "someError" )
                      .build() )
            .build();

        final PropertyTree data = contentDataSerializer.toNodeData( Content.create()
                                                                        .name( "myContent" )
                                                                        .parentPath( ContentPath.ROOT )
                                                                        .creator( PrincipalKey.ofAnonymous() )
                                                                        .modifier( PrincipalKey.ofAnonymous() )
                                                                        .validationErrors( validationErrors )
                                                                        .build() );

        final Iterable<PropertySet> validationErrorsData = data.getSets( "validationErrors" );
        assertThat( validationErrorsData ).hasSize( 3 )
            .extracting( propertySet -> propertySet.getString( "errorCode" ) )
            .containsExactly( "system:SOME_CODE", "system:SOME_OTHER_CODE", "system:SERIOUS_ERROR" );

        assertThat( validationErrorsData ).extracting( propertySet -> propertySet.getString( "attachment" ) )
            .containsExactly( "myName", null, null );
    }

    @Test
    void create_propertyTree_populated_with_extraData()
    {
        final ContentDataSerializer contentDataSerializer = new ContentDataSerializer();

        PropertyTree fragmentData = new PropertyTree();
        fragmentData.setString( "myKey1", "myValue1" );
        fragmentData.setString( "myKey2", "myValue2" );

        final PropertyTree data = contentDataSerializer.toCreateNodeData( CreateContentTranslatorParams.create()
                                                                              .parent( ContentPath.ROOT )
                                                                              .name( "myContentName" )
                                                                              .contentData( new PropertyTree() )
                                                                              .displayName( "myDisplayName" )
                                                                              .type( ContentTypeName.codeMedia() )
                                                                              .creator( PrincipalKey.ofAnonymous() )
                                                                              .childOrder( ChildOrder.defaultOrder() )
                                                                              .extraDatas( Mixins.create()
                                                                                               .add( new Mixin(
                                                                                                   MixinName.from( ApplicationKey.SYSTEM,
                                                                                                                   "myFragment" ),
                                                                                                   fragmentData ) )
                                                                                               .build() )
                                                                              .build() );

        final PropertySet extraData = data.getSet( ContentPropertyNames.MIXINS );
        assertNotNull( extraData );
        final PropertySet systemAppData = extraData.getSet( ApplicationKey.SYSTEM.getName() );
        assertNotNull( systemAppData );
        final PropertySet myFragmentData = systemAppData.getSet( "myFragment" );
        assertNotNull( myFragmentData );
        assertEquals( "myValue1", myFragmentData.getString( "myKey1" ) );
        assertEquals( "myValue1", myFragmentData.getString( "myKey1" ) );
    }


    @Test
    void update_propertyTree_populated_with_extraData()
    {
        final ContentDataSerializer contentDataSerializer = new ContentDataSerializer();

        PropertyTree fragmentData = new PropertyTree();
        fragmentData.setString( "myKey1", "myValue1" );
        fragmentData.setString( "myKey2", "myValue2" );

        final PropertyTree updatedProperties = contentDataSerializer.toNodeData( Content.create()
                                                                                     .name( "myContent" )
                                                                                     .parentPath( ContentPath.ROOT )
                                                                                     .creator( PrincipalKey.ofAnonymous() )
                                                                                     .extraDatas( Mixins.create()
                                                                                                      .add( new Mixin( MixinName.from(
                                                                                                          ApplicationKey.SYSTEM,
                                                                                                          "myFragment" ), fragmentData ) )
                                                                                                      .build() )
                                                                                     .modifier( PrincipalKey.ofAnonymous() )
                                                                                     .build() );

        final PropertySet extraData = updatedProperties.getSet( ContentPropertyNames.MIXINS );
        assertNotNull( extraData );
        final PropertySet systemAppData = extraData.getSet( ApplicationKey.SYSTEM.getName() );
        assertNotNull( systemAppData );
        final PropertySet myFragmentData = systemAppData.getSet( "myFragment" );
        assertNotNull( myFragmentData );
        assertEquals( "myValue1", myFragmentData.getString( "myKey1" ) );
        assertEquals( "myValue1", myFragmentData.getString( "myKey1" ) );
    }


    @Test
    void create_add_content_data()
    {
        final ContentDataSerializer contentDataSerializer = new ContentDataSerializer();

        final PropertyTree propertyTree = new PropertyTree();
        propertyTree.setString( "myData", "myValue" );

        final PropertyTree data = contentDataSerializer.toCreateNodeData( CreateContentTranslatorParams.create()
                                                                              .parent( ContentPath.ROOT )
                                                                              .name( "myContentName" )
                                                                              .contentData( new PropertyTree() )
                                                                              .displayName( "myDisplayName" )
                                                                              .type( ContentTypeName.codeMedia() )
                                                                              .creator( PrincipalKey.ofAnonymous() )
                                                                              .childOrder( ChildOrder.defaultOrder() )
                                                                              .contentData( propertyTree )
                                                                              .build() );

        final PropertySet contentData = data.getSet( "data" );
        assertNotNull( contentData );
        assertNotNull( contentData.getString( "myData" ) );
        assertEquals( "myValue", contentData.getString( "myData" ) );
    }

    @Test
    void create_add_page()
    {
        final PropertyTree tree = new PropertyTree();
        tree.addProperty( "htmlData", ValueFactory.newString( "<img src =\"source\" data-src=\"image://image-id\" src=\"image/123\"/>" ) );

        final Form form = Form.create()
            .addFormItem( Input.create().name( "htmlData" ).label( "htmlData" ).inputType( InputTypeName.HTML_AREA ).build() )
            .build();

        final PageDescriptor pageDescriptor = PageDescriptor.create()
            .config( form )
            .regions( RegionDescriptors.create().build() )
            .key( DescriptorKey.from( "aaa:bbb" ) )
            .build();

        final Page page = Page.create().config( tree ).descriptor( pageDescriptor.getKey() ).build();

        final ContentDataSerializer contentDataSerializer = new ContentDataSerializer();

        final PropertyTree propertyTree = new PropertyTree();
        propertyTree.setString( "myData", "myValue" );

        final PropertyTree data = contentDataSerializer.toCreateNodeData( CreateContentTranslatorParams.create()
                                                                              .parent( ContentPath.ROOT )
                                                                              .name( "myContentName" )
                                                                              .contentData( new PropertyTree() )
                                                                              .displayName( "myDisplayName" )
                                                                              .type( ContentTypeName.codeMedia() )
                                                                              .creator( PrincipalKey.ofAnonymous() )
                                                                              .childOrder( ChildOrder.defaultOrder() )
                                                                              .contentData( propertyTree )
                                                                              .page( page )
                                                                              .build() );

        final PropertySet pageData = data.getSet( "components" ).getSet( "page" );
        assertNotNull( pageData );
        assertEquals( pageDescriptor.getKey().toString(), pageData.getString( "descriptor" ) );
        assertEquals( "<img src =\"source\" data-src=\"image://image-id\" src=\"image/123\"/>",
                      pageData.getSet( "config" ).getSet( "aaa" ).getSet( "bbb" ).getString( "htmlData" ) );
    }

    @Test
    void update_add_content_data()
    {
        final ContentDataSerializer contentDataSerializer = new ContentDataSerializer();

        final PropertyTree propertyTree = new PropertyTree();
        propertyTree.setString( "myData", "myValue" );

        final PropertyTree data = contentDataSerializer.toNodeData( Content.create()
                                                                        .name( "myContent" )
                                                                        .parentPath( ContentPath.ROOT )
                                                                        .creator( PrincipalKey.ofAnonymous() )
                                                                        .modifier( PrincipalKey.ofAnonymous() )
                                                                        .data( propertyTree )
                                                                        .build() );

        final PropertySet contentData = data.getSet( "data" );
        assertNotNull( contentData );
        assertNotNull( contentData.getString( "myData" ) );
        assertEquals( "myValue", contentData.getString( "myData" ) );
    }

    @Test
    void create_propertyTree_populated_with_workflowInfo()
    {
        final String check1Name = "myCheck1";
        final WorkflowCheckState check1State = WorkflowCheckState.APPROVED;

        final String check2Name = "myCheck2";
        final WorkflowCheckState check2State = WorkflowCheckState.PENDING;

        final WorkflowInfo workflowInfo = WorkflowInfo.create()
            .state( WorkflowState.PENDING_APPROVAL )
            .checks( Map.of( check1Name, check1State, check2Name, check2State ) )
            .build();

        final ContentDataSerializer contentDataSerializer = new ContentDataSerializer();

        final CreateContentTranslatorParams params = CreateContentTranslatorParams.create()
            .parent( ContentPath.ROOT )
            .name( "myContentName" )
            .contentData( new PropertyTree() )
            .displayName( "myDisplayName" )
            .type( ContentTypeName.codeMedia() )
            .creator( PrincipalKey.ofAnonymous() )
            .childOrder( ChildOrder.defaultOrder() )
            .workflowInfo( workflowInfo )
            .build();

        final PropertyTree data = contentDataSerializer.toCreateNodeData( params );

        final PropertySet workflowData = data.getSet( ContentPropertyNames.WORKFLOW_INFO );
        assertEquals( workflowInfo.getState().toString(), workflowData.getString( ContentPropertyNames.WORKFLOW_INFO_STATE ) );

        final PropertySet workflowChecks = workflowData.getPropertySet( ContentPropertyNames.WORKFLOW_INFO_CHECKS );
        assertEquals( check1State.toString(), workflowChecks.getString( check1Name ) );
        assertEquals( check2State.toString(), workflowChecks.getString( check2Name ) );
    }

    @Test
    void update_add_workflow_info()
    {
        final ContentDataSerializer contentDataSerializer = new ContentDataSerializer();

        final PropertyTree propertyTree = new PropertyTree();
        propertyTree.setString( "myData", "myValue" );

        final PropertyTree data = contentDataSerializer.toNodeData( Content.create()
                                                                        .name( "myContent" )
                                                                        .parentPath( ContentPath.ROOT )
                                                                        .creator( PrincipalKey.ofAnonymous() )
                                                                        .data( propertyTree )
                                                                        .modifier( PrincipalKey.ofAnonymous() )
                                                                        .build() );

        final PropertySet workflowData = data.getSet( ContentPropertyNames.WORKFLOW_INFO );
        assertEquals( WorkflowState.READY.toString(), workflowData.getString( ContentPropertyNames.WORKFLOW_INFO_STATE ) );
    }
}
