package com.enonic.xp.core.impl.content;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentName;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.site.Site;

public final class ContentFixture
{
    public static Node someContentNode()
    {
        return someContentNode( "my-content" );
    }

    public static Node someContentNode( final String name )
    {
        final PropertyTree nodeContentData = new PropertyTree();

        nodeContentData.setString( "type", ContentTypeName.folder().toString() );
        nodeContentData.setSet( "data", nodeContentData.newSet() );
        nodeContentData.setString( "creator", "user:system:su" );

        return Node.create()
            .id( NodeId.from( name ) )
            .name( name )
            .parentPath( new NodePath( "/content" ) )
            .data( nodeContentData )
            .nodeType( ContentConstants.CONTENT_NODE_COLLECTION )
            .build();
    }

    public static Node mockContentNode( final Content content )
    {
        final PropertyTree nodeContentData = new PropertyTree();

        nodeContentData.setString( "type", content.getType().toString() );
        nodeContentData.setSet( "data", content.getData().getRoot().copy( nodeContentData ) );
        nodeContentData.setString( "creator", content.getCreator().toString() );
        nodeContentData.addStrings( "inherit", content.getInherit().stream().map( Object::toString ).toList() );
        return Node.create()
            .id( NodeId.from( content.getId() ) )
            .name( content.getName().toString() )
            .parentPath( ContentNodeHelper.translateContentPathToNodePath( content.getParentPath() ) )
            .data( nodeContentData ).nodeType( ContentConstants.CONTENT_NODE_COLLECTION )
            .build();
    }

    public static Content mockContent( ContentPath parentPath, String name )
    {
        final PropertyTree contentData = new PropertyTree();
        contentData.addString( "property", "value" );

        return Content.create()
            .id( ContentId.from( "id" ) )
            .name( ContentName.from( name ) )
            .type( ContentTypeName.folder() )
            .parentPath( parentPath )
            .creator( PrincipalKey.ofAnonymous() )
            .data( contentData )
            .build();
    }

    public static Content mockContent()
    {
        return mockContent( ContentPath.ROOT, "some-content" );
    }

    public static Site mockSite( String parent, String name )
    {
        final PropertyTree contentData = new PropertyTree();
        contentData.addString( "property", "value" );

        return Site.create()
            .id( ContentId.from( "id" ) )
            .name( ContentName.from( name ) )
            .type( ContentTypeName.site() )
            .parentPath( ContentPath.from( parent ) )
            .creator( PrincipalKey.ofAnonymous() )
            .data( contentData )
            .build();
    }

    public static Site mockSite()
    {
        return mockSite( ContentPath.ROOT.toString(), "some-site" );
    }
}
