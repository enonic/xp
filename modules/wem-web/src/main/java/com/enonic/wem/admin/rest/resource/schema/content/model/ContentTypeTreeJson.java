package com.enonic.wem.admin.rest.resource.schema.content.model;

import java.util.List;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.support.tree.Tree;
import com.enonic.wem.api.support.tree.TreeNode;

public class ContentTypeTreeJson
{
    private int total;

    private final ImmutableList<ContentTypeTreeNodeJson> list;

    public ContentTypeTreeJson( final Tree<ContentType> tree )
    {
        Preconditions.checkNotNull( tree );

        this.total = tree.deepSize();

        final ImmutableList.Builder<ContentTypeTreeNodeJson> builder = ImmutableList.builder();
        for ( final TreeNode<ContentType> rootNode : tree )
        {
            builder.add( new ContentTypeTreeNodeJson( rootNode ) );
        }

        this.list = builder.build();
    }

    public int getTotal()
    {
        return this.total;
    }

    public List<ContentTypeTreeNodeJson> getContentTypes()
    {
        return this.list;
    }
}
