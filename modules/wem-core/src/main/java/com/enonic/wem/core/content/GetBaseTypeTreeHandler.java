package com.enonic.wem.core.content;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;

import com.enonic.wem.api.command.content.GetBaseTypeTree;
import com.enonic.wem.api.content.BaseType;
import com.enonic.wem.api.content.BaseTypeKind;
import com.enonic.wem.api.content.mixin.Mixins;
import com.enonic.wem.api.content.relationshiptype.RelationshipTypes;
import com.enonic.wem.api.content.type.ContentType;
import com.enonic.wem.api.support.tree.Tree;
import com.enonic.wem.api.support.tree.TreeNode;
import com.enonic.wem.core.command.CommandContext;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.content.mixin.dao.MixinDao;
import com.enonic.wem.core.content.relationshiptype.dao.RelationshipTypeDao;
import com.enonic.wem.core.content.type.ContentTypeTreeFactory;
import com.enonic.wem.core.content.type.dao.ContentTypeDao;

@Component
public class GetBaseTypeTreeHandler
    extends CommandHandler<GetBaseTypeTree>
{
    private ContentTypeDao contentTypeDao;

    private MixinDao mixinDao;

    private RelationshipTypeDao relationshipTypeDao;

    public GetBaseTypeTreeHandler()
    {
        super( GetBaseTypeTree.class );
    }

    @Override
    public void handle( final CommandContext context, final GetBaseTypeTree command )
        throws Exception
    {
        final Tree<BaseType> typesTree = new Tree<BaseType>();
        if ( command.isIncludeType( BaseTypeKind.CONTENT_TYPE ) )
        {
            // add all all super content types at root
            final Tree<ContentType> contentTypeTree = new ContentTypeTreeFactory( context.getJcrSession(), contentTypeDao ).createTree();
            typesTree.addNodes( extractRootContentTypes( contentTypeTree ) );
        }

        if ( command.isIncludeType( BaseTypeKind.RELATIONSHIP_TYPE ) )
        {
            // add all RelationshipTypes on root
            final RelationshipTypes relationshipTypes = relationshipTypeDao.selectAll( context.getJcrSession() );
            typesTree.createNodes( relationshipTypes );
        }

        if ( command.isIncludeType( BaseTypeKind.MIXIN ) )
        {
            // add all Mixins on root
            final Mixins mixins = mixinDao.selectAll( context.getJcrSession() );
            typesTree.createNodes( mixins );
        }

        command.setResult( typesTree );
    }

    private List<TreeNode<BaseType>> extractRootContentTypes( final Tree<ContentType> contentTypeTree )
    {
        final List<TreeNode<BaseType>> list = Lists.newArrayList();
        for ( TreeNode<ContentType> node : contentTypeTree )
        {
            final TreeNode<BaseType> treeNode = new TreeNode<BaseType>( node.getObject() );
            extractChildren( treeNode, node );
            list.add( treeNode );
        }
        return list;
    }

    private void extractChildren( final TreeNode<BaseType> toParent, final TreeNode<ContentType> fromParent )
    {
        for ( TreeNode<ContentType> fromChild : fromParent )
        {
            TreeNode<BaseType> child = new TreeNode<BaseType>( fromChild.getObject() );
            toParent.addChild( child );
            extractChildren( child, fromChild );
        }
    }


    @Autowired
    public void setContentTypeDao( final ContentTypeDao contentTypeDao )
    {
        this.contentTypeDao = contentTypeDao;
    }

    @Autowired
    public void setRelationshipTypeDao( final RelationshipTypeDao relationshipTypeDao )
    {
        this.relationshipTypeDao = relationshipTypeDao;
    }

    @Autowired
    public void setMixinDao( final MixinDao mixinDao )
    {
        this.mixinDao = mixinDao;
    }
}
