package com.enonic.xp.core.impl.content;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.enonic.xp.attachment.AttachmentNames;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentPropertyNames;
import com.enonic.xp.content.CreateContentTranslatorParams;
import com.enonic.xp.data.PropertyPath;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.index.IndexConfig;
import com.enonic.xp.index.IndexConfigDocument;
import com.enonic.xp.node.AttachedBinaries;
import com.enonic.xp.node.AttachedBinary;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeName;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.UserStoreKey;
import com.enonic.xp.security.acl.AccessControlEntry;
import com.enonic.xp.security.acl.AccessControlList;
import com.enonic.xp.security.acl.Permission;
import com.enonic.xp.util.BinaryReference;

import static org.junit.Assert.*;

public class ContentNodeTranslatorTest
{
    private static final String CONTENT_DATA_PREFIX = ContentPropertyNames.DATA;

    private ContentNodeTranslator translator;

    @Before
    public void before()
    {
        translator = new ContentNodeTranslator();
    }

    @Test
    public void toCreateNode_contentData_to_rootdataset()
        throws Exception
    {
        final PropertyTree contentData = new PropertyTree( new PropertyTree.PredictivePropertyIdProvider() );
        contentData.setString( "test", "testValue" );

        final CreateContentTranslatorParams mycontent = CreateContentTranslatorParams.create().
            name( "mycontent" ).
            displayName( "myDisplayName" ).
            parent( ContentPath.ROOT ).
            type( ContentTypeName.from( "mymodule:my-content-type" ) ).
            contentData( contentData ).
            creator( PrincipalKey.ofAnonymous() ).
            childOrder( ContentConstants.DEFAULT_CHILD_ORDER ).
            build();

        final CreateNodeParams createNode = translator.toCreateNodeParams( mycontent );

        assertEquals( "testValue", createNode.getData().getString( CONTENT_DATA_PREFIX + ".test" ) );
    }

    @Test
    public void translate_entityIndexConfig_decide_by_type_for_contentdata()
        throws Exception
    {
        final PropertyTree contentData = new PropertyTree( new PropertyTree.PredictivePropertyIdProvider() );
        contentData.setString( "test", "testValue" );

        final CreateContentTranslatorParams mycontent = CreateContentTranslatorParams.create().
            name( "mycontent" ).
            displayName( "myDisplayName" ).
            parent( ContentPath.ROOT ).
            type( ContentTypeName.from( "mymodule:my-content-type" ) ).
            contentData( contentData ).
            creator( PrincipalKey.ofAnonymous() ).
            childOrder( ContentConstants.DEFAULT_CHILD_ORDER ).
            build();

        final CreateNodeParams createNode = translator.toCreateNodeParams( mycontent );

        final IndexConfigDocument indexConfigDocument = createNode.getIndexConfigDocument();

        final IndexConfig configForData = indexConfigDocument.getConfigForPath( PropertyPath.from( CONTENT_DATA_PREFIX + ".test" ) );

        assertNotNull( configForData );
        assertEquals( true, configForData.isEnabled() );
        assertEquals( true, configForData.isDecideByType() );
    }

    @Test
    public void node_to_content_thumbnail()
    {
        AttachedBinaries attachedBinaries = AttachedBinaries.create().
            add( new AttachedBinary( BinaryReference.from( AttachmentNames.THUMBNAIL ), "myBlobKey" ) ).
            build();

        PropertyTree data = new PropertyTree( new PropertyTree.PredictivePropertyIdProvider() );
        data.addString( "type", "my-type" );
        data.addBoolean( "valid", true );
        data.addSet( "data" );
        data.addSet( "form" );
        data.addString( ContentPropertyNames.CREATOR, "user:system:rmy" );
        PropertySet attachmentSet = data.addSet( "attachment" );
        attachmentSet.addString( "name", AttachmentNames.THUMBNAIL );
        attachmentSet.addString( "mimeType", "image/png" );
        attachmentSet.addLong( "size", 1L );

        Node node = Node.newNode().id( NodeId.from( "myId" ) ).
            attachedBinaries( attachedBinaries ).
            parentPath( NodePath.ROOT ).
            path( "myPath" ).
            name( NodeName.from( "myname" ) ).
            data( data ).
            build();

        Content content = translator.fromNode( node );

        Assert.assertNotNull( content.getThumbnail() );
    }

    @Test
    public void node_to_content_acl()
    {
        final AccessControlEntry entry1 = AccessControlEntry.create().
            principal( PrincipalKey.ofAnonymous() ).
            allow( Permission.READ ).
            deny( Permission.DELETE ).
            build();
        final AccessControlEntry entry2 = AccessControlEntry.create().
            principal( PrincipalKey.ofUser( UserStoreKey.system(), "user1" ) ).
            allow( Permission.MODIFY ).
            deny( Permission.PUBLISH ).
            build();
        AccessControlList acl = AccessControlList.create().add( entry1 ).add( entry2 ).build();

        final PropertyTree contentAsData = new PropertyTree( new PropertyTree.PredictivePropertyIdProvider() );
        contentAsData.addString( "type", "my-type" );
        contentAsData.addBoolean( "valid", true );
        contentAsData.addSet( "data" );
        contentAsData.addSet( "form" );
        contentAsData.addString( ContentPropertyNames.CREATOR, "user:system:rmy" );

        final Node node = Node.newNode().id( NodeId.from( "myId" ) ).
            parentPath( NodePath.ROOT ).
            path( "myPath" ).
            name( NodeName.from( "myname" ) ).
            data( contentAsData ).
            permissions( acl ).
            build();

        final Content content = translator.fromNode( node );

        Assert.assertNotNull( content.getPermissions() );
    }

}
