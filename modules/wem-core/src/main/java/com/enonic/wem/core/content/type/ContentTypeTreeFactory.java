package com.enonic.wem.core.content.type;


import java.util.List;

import javax.jcr.Session;

import com.google.common.collect.ArrayListMultimap;

import com.enonic.wem.api.content.type.ContentType;
import com.enonic.wem.api.content.type.ContentTypes;
import com.enonic.wem.api.content.type.QualifiedContentTypeName;
import com.enonic.wem.api.support.tree.Tree;
import com.enonic.wem.api.support.tree.TreeNode;
import com.enonic.wem.core.content.type.dao.ContentTypeDao;

final class ContentTypeTreeFactory
{
    private final ContentTypeDao contentTypeDao;

    private final Session session;

    ContentTypeTreeFactory( final Session session, final ContentTypeDao contentTypeDao )
    {
        this.contentTypeDao = contentTypeDao;
        this.session = session;
    }

    Tree<ContentType> createTree()
    {
        final ContentTypes contentTypes = contentTypeDao.retrieveAllContentTypes( session );
        return buildTree( contentTypes );
    }

    private Tree<ContentType> buildTree( final ContentTypes contentTypes )
    {
        final ArrayListMultimap<QualifiedContentTypeName, ContentType> inheritingTypesBySuperType = ArrayListMultimap.create();

        final Tree<ContentType> tree = new Tree<ContentType>();

        for ( ContentType contentType : contentTypes )
        {
            if ( contentType.getSuperType() == null )
            {
                tree.createNode( contentType );
            }
            else
            {
                inheritingTypesBySuperType.put( contentType.getSuperType(), contentType );
            }
        }

        for ( TreeNode<ContentType> rootNodes : tree )
        {
            addChildren( rootNodes, inheritingTypesBySuperType );
        }

        return tree;
    }

    private void addChildren( final TreeNode<ContentType> parentNode,
                              final ArrayListMultimap<QualifiedContentTypeName, ContentType> inheritingTypesBySuperType )
    {
        // Find all content types inheriting the type in parentNode
        final List<ContentType> inheritingContentTypes = inheritingTypesBySuperType.get( parentNode.getObject().getQualifiedName() );
        // ...and add them under the parent
        for ( ContentType contentType : inheritingContentTypes )
        {
            final TreeNode<ContentType> contentTypeTreeNode = parentNode.addChild( contentType );

            // continue recursively...
            addChildren( contentTypeTreeNode, inheritingTypesBySuperType );
        }
    }
}
