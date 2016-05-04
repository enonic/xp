package com.enonic.xp.core.impl.content.serializer;

import org.junit.Test;

import com.google.common.io.ByteSource;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.attachment.CreateAttachment;
import com.enonic.xp.attachment.CreateAttachments;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentPropertyNames;
import com.enonic.xp.content.CreateContentTranslatorParams;
import com.enonic.xp.content.ExtraData;
import com.enonic.xp.content.ExtraDatas;
import com.enonic.xp.content.UpdateContentTranslatorParams;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.index.ChildOrder;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.mixin.MixinName;
import com.enonic.xp.security.PrincipalKey;

import static org.junit.Assert.*;

public class ContentDataSerializerTest
{
    @Test
    public void create_propertyTree_populated_with_attachment_properties()
        throws Exception
    {
        final ContentDataSerializer contentDataSerializer = new ContentDataSerializer();

        final String binaryName = "myName";
        final String binaryLabel = "myLabel";
        final String binaryMimeType = "myMimeType";
        final byte[] binaryData = "my binary".getBytes();

        final CreateContentTranslatorParams params = CreateContentTranslatorParams.create().
            parent( ContentPath.ROOT ).
            name( "myContentName" ).
            contentData( new PropertyTree() ).
            displayName( "myDisplayName" ).
            type( ContentTypeName.codeMedia() ).
            creator( PrincipalKey.ofAnonymous() ).
            childOrder( ChildOrder.defaultOrder() ).
            createAttachments( CreateAttachments.from( CreateAttachment.create().
                byteSource( ByteSource.wrap( binaryData ) ).
                label( binaryLabel ).
                mimeType( binaryMimeType ).
                name( binaryName ).
                build() ) ).
            build();

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
        throws Exception
    {
        final ContentDataSerializer contentDataSerializer = new ContentDataSerializer();

        final String binaryName = "myName";
        final String binaryLabel = "myLabel";
        final String binaryMimeType = "myMimeType";
        final byte[] binaryData = "my binary".getBytes();

        final UpdateContentTranslatorParams params = UpdateContentTranslatorParams.create().
            editedContent( Content.create().
                name( "myContent" ).
                parentPath( ContentPath.ROOT ).
                creator( PrincipalKey.ofAnonymous() ).
                build() ).
            modifier( PrincipalKey.ofAnonymous() ).
            createAttachments( CreateAttachments.from( CreateAttachment.create().
                byteSource( ByteSource.wrap( binaryData ) ).
                label( binaryLabel ).
                mimeType( binaryMimeType ).
                name( binaryName ).
                build() ) ).
            build();

        final PropertyTree data = contentDataSerializer.toUpdateNodeData( params );

        final PropertySet attachmentData = data.getSet( ContentPropertyNames.ATTACHMENT );
        assertNotNull( attachmentData );
        assertEquals( binaryName, attachmentData.getString( ContentPropertyNames.ATTACHMENT_NAME ) );
        assertEquals( binaryLabel, attachmentData.getString( ContentPropertyNames.ATTACHMENT_LABEL ) );
        assertEquals( binaryMimeType, attachmentData.getString( ContentPropertyNames.ATTACHMENT_MIMETYPE ) );
        assertEquals( binaryData.length + "", attachmentData.getString( ContentPropertyNames.ATTACHMENT_SIZE ) );
        assertEquals( binaryName, attachmentData.getString( ContentPropertyNames.ATTACHMENT_BINARY_REF ) );
    }


    @Test
    public void create_propertyTree_populated_with_extraData()
        throws Exception
    {
        final ContentDataSerializer contentDataSerializer = new ContentDataSerializer();

        PropertyTree mixinData = new PropertyTree();
        mixinData.setString( "myKey1", "myValue1" );
        mixinData.setString( "myKey2", "myValue2" );

        final PropertyTree data = contentDataSerializer.toCreateNodeData( CreateContentTranslatorParams.create().
            parent( ContentPath.ROOT ).
            name( "myContentName" ).
            contentData( new PropertyTree() ).
            displayName( "myDisplayName" ).
            type( ContentTypeName.codeMedia() ).
            creator( PrincipalKey.ofAnonymous() ).
            childOrder( ChildOrder.defaultOrder() ).
            extraDatas( ExtraDatas.create().
                add( new ExtraData( MixinName.from( ApplicationKey.SYSTEM, "myMixin" ), mixinData ) ).
                build() ).
            build() );

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
        throws Exception
    {
        final ContentDataSerializer contentDataSerializer = new ContentDataSerializer();

        PropertyTree mixinData = new PropertyTree();
        mixinData.setString( "myKey1", "myValue1" );
        mixinData.setString( "myKey2", "myValue2" );

        final PropertyTree updatedProperties = contentDataSerializer.toUpdateNodeData( UpdateContentTranslatorParams.create().
            editedContent( Content.create().
                name( "myContent" ).
                parentPath( ContentPath.ROOT ).
                creator( PrincipalKey.ofAnonymous() ).
                extraDatas( ExtraDatas.create().
                    add( new ExtraData( MixinName.from( ApplicationKey.SYSTEM, "myMixin" ), mixinData ) ).
                    build() ).
                build() ).
            modifier( PrincipalKey.ofAnonymous() ).
            build() );

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
        throws Exception
    {
        final ContentDataSerializer contentDataSerializer = new ContentDataSerializer();

        final PropertyTree propertyTree = new PropertyTree();
        propertyTree.setString( "myData", "myValue" );

        final PropertyTree data = contentDataSerializer.toCreateNodeData( CreateContentTranslatorParams.create().
            parent( ContentPath.ROOT ).
            name( "myContentName" ).
            contentData( new PropertyTree() ).
            displayName( "myDisplayName" ).
            type( ContentTypeName.codeMedia() ).
            creator( PrincipalKey.ofAnonymous() ).
            childOrder( ChildOrder.defaultOrder() ).
            contentData( propertyTree ).
            build() );

        final PropertySet contentData = data.getSet( "data" );
        assertNotNull( contentData );
        assertNotNull( contentData.getString( "myData" ) );
        assertEquals( "myValue", contentData.getString( "myData" ) );
    }

    @Test
    public void update_add_content_data()
        throws Exception
    {
        final ContentDataSerializer contentDataSerializer = new ContentDataSerializer();

        final PropertyTree propertyTree = new PropertyTree();
        propertyTree.setString( "myData", "myValue" );

        final PropertyTree data = contentDataSerializer.toUpdateNodeData( UpdateContentTranslatorParams.create().
            editedContent( Content.create().
                name( "myContent" ).
                parentPath( ContentPath.ROOT ).
                creator( PrincipalKey.ofAnonymous() ).
                data( propertyTree ).
                build() ).
            modifier( PrincipalKey.ofAnonymous() ).
            build() );

        final PropertySet contentData = data.getSet( "data" );
        assertNotNull( contentData );
        assertNotNull( contentData.getString( "myData" ) );
        assertEquals( "myValue", contentData.getString( "myData" ) );
    }

}

