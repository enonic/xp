package com.enonic.xp.core.impl.content.serializer;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.google.common.io.ByteSource;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.attachment.Attachment;
import com.enonic.xp.attachment.Attachments;
import com.enonic.xp.attachment.CreateAttachment;
import com.enonic.xp.attachment.CreateAttachments;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentPropertyNames;
import com.enonic.xp.content.CreateContentTranslatorParams;
import com.enonic.xp.content.ExtraData;
import com.enonic.xp.content.ExtraDatas;
import com.enonic.xp.content.ValidationError;
import com.enonic.xp.content.ValidationErrorCode;
import com.enonic.xp.content.ValidationErrors;
import com.enonic.xp.content.WorkflowCheckState;
import com.enonic.xp.content.WorkflowInfo;
import com.enonic.xp.content.WorkflowState;
import com.enonic.xp.data.PropertyPath;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.index.ChildOrder;
import com.enonic.xp.page.PageDescriptorService;
import com.enonic.xp.region.LayoutDescriptorService;
import com.enonic.xp.region.PartDescriptorService;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.xdata.XDataName;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.util.BinaryReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ContentDataSerializerTest
{
    @Test
    public void create_propertyTree_populated_with_attachment_properties()
    {
        final ContentDataSerializer contentDataSerializer = createContentDataSerializer();

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
    public void update_propertyTree_populated_with_new_attachment_properties()
    {
        final ContentDataSerializer contentDataSerializer = createContentDataSerializer();

        final String binaryName = "myName";
        final String binaryLabel = "myLabel";
        final String binaryMimeType = "myMimeType";

        final Content content = Content.create()
            .name( "myContent" )
            .parentPath( ContentPath.ROOT )
            .creator( PrincipalKey.ofAnonymous() )
            .validationErrors( ValidationErrors.create()
                                   .add( ValidationError.attachmentError( ValidationErrorCode.from( ApplicationKey.SYSTEM, "SOME_CODE" ),
                                                                          BinaryReference.from( "prevFile" ) )
                                             .message( "someError" )
                                             .build() )
                                   .build() )
            .build();
        final PropertyTree data = contentDataSerializer.toUpdateNodeData( content, PrincipalKey.ofAnonymous(), Attachments.from(
            Attachment.create().label( binaryLabel ).size( 11 ).mimeType( binaryMimeType ).name( binaryName ).build() ) );

        final PropertySet attachmentData = data.getSet( ContentPropertyNames.ATTACHMENT );
        assertNotNull( attachmentData );
        assertEquals( binaryName, attachmentData.getString( ContentPropertyNames.ATTACHMENT_NAME ) );
        assertEquals( binaryLabel, attachmentData.getString( ContentPropertyNames.ATTACHMENT_LABEL ) );
        assertEquals( binaryMimeType, attachmentData.getString( ContentPropertyNames.ATTACHMENT_MIMETYPE ) );
        assertEquals( "11", attachmentData.getString( ContentPropertyNames.ATTACHMENT_SIZE ) );
        assertEquals( binaryName, attachmentData.getString( ContentPropertyNames.ATTACHMENT_BINARY_REF ) );
    }


    @Test
    public void update_validationErrors()
    {
        final ContentDataSerializer contentDataSerializer = createContentDataSerializer();

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

        final PropertyTree data = contentDataSerializer.toUpdateNodeData( Content.create()
                                                                              .name( "myContent" )
                                                                              .parentPath( ContentPath.ROOT )
                                                                              .creator( PrincipalKey.ofAnonymous() )
                                                                              .validationErrors( validationErrors )
                                                                              .build(), PrincipalKey.ofAnonymous(), Attachments.empty() );

        final Iterable<PropertySet> validationErrorsData = data.getSets( "validationErrors" );
        assertThat( validationErrorsData ).hasSize( 3 )
            .extracting( propertySet -> propertySet.getString( "errorCode" ) )
            .containsExactly( "system:SOME_CODE", "system:SOME_OTHER_CODE", "system:SERIOUS_ERROR" );

        assertThat( validationErrorsData ).extracting( propertySet -> propertySet.getString( "attachment" ) )
            .containsExactly( "myName", null, null );
    }

    @Test
    public void create_propertyTree_populated_with_extraData()
    {
        final ContentDataSerializer contentDataSerializer = createContentDataSerializer();

        PropertyTree mixinData = new PropertyTree();
        mixinData.setString( "myKey1", "myValue1" );
        mixinData.setString( "myKey2", "myValue2" );

        final PropertyTree data = contentDataSerializer.toCreateNodeData( CreateContentTranslatorParams.create()
                                                                              .parent( ContentPath.ROOT )
                                                                              .name( "myContentName" )
                                                                              .contentData( new PropertyTree() )
                                                                              .displayName( "myDisplayName" )
                                                                              .type( ContentTypeName.codeMedia() )
                                                                              .creator( PrincipalKey.ofAnonymous() )
                                                                              .childOrder( ChildOrder.defaultOrder() )
                                                                              .extraDatas( ExtraDatas.create()
                                                                                               .add( new ExtraData(
                                                                                                   XDataName.from( ApplicationKey.SYSTEM,
                                                                                                                   "myMixin" ),
                                                                                                   mixinData ) )
                                                                                               .build() )
                                                                              .build() );

        final PropertySet extraData = data.getSet( ContentPropertyNames.EXTRA_DATA );
        assertNotNull( extraData );
        final PropertySet systemAppData = extraData.getSet( ApplicationKey.SYSTEM.getName() );
        assertNotNull( systemAppData );
        final PropertySet myMixinData = systemAppData.getSet( "myMixin" );
        assertNotNull( myMixinData );
        assertEquals( "myValue1", myMixinData.getString( "myKey1" ) );
        assertEquals( "myValue1", myMixinData.getString( "myKey1" ) );
    }


    @Test
    public void update_propertyTree_populated_with_extraData()
    {
        final ContentDataSerializer contentDataSerializer = createContentDataSerializer();

        PropertyTree mixinData = new PropertyTree();
        mixinData.setString( "myKey1", "myValue1" );
        mixinData.setString( "myKey2", "myValue2" );

        final PropertyTree updatedProperties = contentDataSerializer.toUpdateNodeData( Content.create()
                                                                                           .name( "myContent" )
                                                                                           .parentPath( ContentPath.ROOT )
                                                                                           .creator( PrincipalKey.ofAnonymous() )
                                                                                           .extraDatas( ExtraDatas.create()
                                                                                                            .add( new ExtraData(
                                                                                                                XDataName.from(
                                                                                                                    ApplicationKey.SYSTEM,
                                                                                                                    "myMixin" ),
                                                                                                                mixinData ) )
                                                                                                            .build() )
                                                                                           .build(), PrincipalKey.ofAnonymous(),
                                                                                       Attachments.empty() );

        final PropertySet extraData = updatedProperties.getSet( ContentPropertyNames.EXTRA_DATA );
        assertNotNull( extraData );
        final PropertySet systemAppData = extraData.getSet( ApplicationKey.SYSTEM.getName() );
        assertNotNull( systemAppData );
        final PropertySet myMixinData = systemAppData.getSet( "myMixin" );
        assertNotNull( myMixinData );
        assertEquals( "myValue1", myMixinData.getString( "myKey1" ) );
        assertEquals( "myValue1", myMixinData.getString( "myKey1" ) );
    }


    @Test
    public void create_add_content_data()
    {
        final ContentDataSerializer contentDataSerializer = createContentDataSerializer();

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
    public void update_add_content_data()
    {
        final ContentDataSerializer contentDataSerializer = createContentDataSerializer();

        final PropertyTree propertyTree = new PropertyTree();
        propertyTree.setString( "myData", "myValue" );

        final PropertyTree data = contentDataSerializer.toUpdateNodeData( Content.create()
                                                                              .name( "myContent" )
                                                                              .parentPath( ContentPath.ROOT )
                                                                              .creator( PrincipalKey.ofAnonymous() )
                                                                              .data( propertyTree )
                                                                              .build(), PrincipalKey.ofAnonymous(), Attachments.empty() );

        final PropertySet contentData = data.getSet( "data" );
        assertNotNull( contentData );
        assertNotNull( contentData.getString( "myData" ) );
        assertEquals( "myValue", contentData.getString( "myData" ) );
    }

    private ContentDataSerializer createContentDataSerializer()
    {
        final PageDescriptorService pageDescriptorService = Mockito.mock( PageDescriptorService.class );
        final PartDescriptorService partDescriptorService = Mockito.mock( PartDescriptorService.class );
        final LayoutDescriptorService layoutDescriptorService = Mockito.mock( LayoutDescriptorService.class );

        return ContentDataSerializer.create()
            .partDescriptorService( partDescriptorService )
            .pageDescriptorService( pageDescriptorService )
            .layoutDescriptorService( layoutDescriptorService )
            .build();
    }

    @Test
    public void create_propertyTree_populated_with_workflowInfo()
    {
        final String check1Name = "myCheck1";
        final WorkflowCheckState check1State = WorkflowCheckState.APPROVED;

        final String check2Name = "myCheck2";
        final WorkflowCheckState check2State = WorkflowCheckState.PENDING;

        final WorkflowInfo workflowInfo = WorkflowInfo.create()
            .state( WorkflowState.PENDING_APPROVAL )
            .checks( Map.of( check1Name, check1State, check2Name, check2State ) )
            .build();

        final ContentDataSerializer contentDataSerializer = createContentDataSerializer();

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
    public void update_add_workflow_info()
    {
        final ContentDataSerializer contentDataSerializer = createContentDataSerializer();

        final PropertyTree propertyTree = new PropertyTree();
        propertyTree.setString( "myData", "myValue" );

        final PropertyTree data = contentDataSerializer.toUpdateNodeData( Content.create()
                                                                              .name( "myContent" )
                                                                              .parentPath( ContentPath.ROOT )
                                                                              .creator( PrincipalKey.ofAnonymous() )
                                                                              .data( propertyTree )
                                                                              .build(), PrincipalKey.ofAnonymous(), Attachments.empty() );

        final PropertySet workflowData = data.getSet( ContentPropertyNames.WORKFLOW_INFO );
        assertEquals( WorkflowState.READY.toString(), workflowData.getString( ContentPropertyNames.WORKFLOW_INFO_STATE ) );
    }
}
