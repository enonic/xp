package com.enonic.wem.admin.json.schema;

import java.util.List;

import com.google.common.collect.ImmutableList;

import com.enonic.wem.admin.json.schema.content.ContentTypeJson;
import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.support.tree.TreeNode;

public class ContentTypeTreeNodeJson
    extends ContentTypeJson
{
    private boolean hasChildren;

    private final ImmutableList<ContentTypeTreeNodeJson> list;

    public ContentTypeTreeNodeJson( final TreeNode<ContentType> node )
    {
        super( node.getObject() );

        this.hasChildren = node.hasChildren();

        final ImmutableList.Builder<ContentTypeTreeNodeJson> builder = ImmutableList.builder();
        for ( final TreeNode<ContentType> subNode : node.getChildren() )
        {
            builder.add( new ContentTypeTreeNodeJson( subNode ) );
        }

        this.list = builder.build();
    }

    public boolean isHasChildren()
    {
        return hasChildren;
    }


    public List<ContentTypeTreeNodeJson> getContentTypes()
    {
        return this.list;
    }
}
