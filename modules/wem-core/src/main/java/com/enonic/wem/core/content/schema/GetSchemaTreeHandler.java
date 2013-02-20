package com.enonic.wem.core.content.schema;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;

import com.enonic.wem.api.command.content.schema.GetSchemaTree;
import com.enonic.wem.api.content.schema.Schema;
import com.enonic.wem.api.content.schema.SchemaKind;
import com.enonic.wem.api.content.schema.content.ContentType;
import com.enonic.wem.api.content.schema.mixin.Mixins;
import com.enonic.wem.api.content.schema.relationship.RelationshipTypes;
import com.enonic.wem.api.support.tree.Tree;
import com.enonic.wem.api.support.tree.TreeNode;
import com.enonic.wem.core.command.CommandContext;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.content.schema.content.ContentTypeTreeFactory;
import com.enonic.wem.core.content.schema.content.dao.ContentTypeDao;
import com.enonic.wem.core.content.schema.mixin.dao.MixinDao;
import com.enonic.wem.core.content.schema.relationship.dao.RelationshipTypeDao;

@Component
public class GetSchemaTreeHandler
    extends CommandHandler<GetSchemaTree>
{
    private ContentTypeDao contentTypeDao;

    private MixinDao mixinDao;

    private RelationshipTypeDao relationshipTypeDao;

    public GetSchemaTreeHandler()
    {
        super( GetSchemaTree.class );
    }

    @Override
    public void handle( final CommandContext context, final GetSchemaTree command )
        throws Exception
    {
        final Tree<Schema> typesTree = new Tree<Schema>();
        if ( command.isIncludingKind( SchemaKind.CONTENT_TYPE ) )
        {
            // add all all super content types at root
            final Tree<ContentType> contentTypeTree = new ContentTypeTreeFactory( context.getJcrSession(), contentTypeDao ).createTree();
            typesTree.addNodes( extractRootContentTypes( contentTypeTree ) );
        }

        if ( command.isIncludingKind( SchemaKind.RELATIONSHIP_TYPE ) )
        {
            // add all RelationshipTypes on root
            final RelationshipTypes relationshipTypes = relationshipTypeDao.selectAll( context.getJcrSession() );
            typesTree.createNodes( relationshipTypes );
        }

        if ( command.isIncludingKind( SchemaKind.MIXIN ) )
        {
            // add all Mixins on root
            final Mixins mixins = mixinDao.selectAll( context.getJcrSession() );
            typesTree.createNodes( mixins );
        }

        command.setResult( typesTree );
    }

    private List<TreeNode<Schema>> extractRootContentTypes( final Tree<ContentType> contentTypeTree )
    {
        final List<TreeNode<Schema>> list = Lists.newArrayList();
        for ( TreeNode<ContentType> node : contentTypeTree )
        {
            final TreeNode<Schema> treeNode = new TreeNode<Schema>( node.getObject() );
            extractChildren( treeNode, node );
            list.add( treeNode );
        }
        return list;
    }

    private void extractChildren( final TreeNode<Schema> toParent, final TreeNode<ContentType> fromParent )
    {
        for ( TreeNode<ContentType> fromChild : fromParent )
        {
            TreeNode<Schema> child = new TreeNode<Schema>( fromChild.getObject() );
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
