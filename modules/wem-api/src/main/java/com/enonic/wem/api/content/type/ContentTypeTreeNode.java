package com.enonic.wem.api.content.type;

import java.util.List;

import com.google.common.collect.ImmutableList;

public final class ContentTypeTreeNode
{
    private final ContentTypeTreeNode parent;

    private final ContentType contentType;

    private ImmutableList<ContentTypeTreeNode> contentTypeTreeNodes;

    ContentTypeTreeNode( final ContentTypeTreeNode parent, final ContentType contentType, final ContentTypeTreeNode... children )
    {
        this.parent = parent;
        this.contentType = contentType;
        this.contentTypeTreeNodes = ImmutableList.copyOf( children );
    }

    public ContentTypeTreeNode parent()
    {
        return parent;
    }

    public int size()
    {
        return contentTypeTreeNodes.size();
    }

    public int deepSize()
    {
        int deepSize = size();
        for ( ContentTypeTreeNode contentTypeNode : contentTypeTreeNodes )
        {
            deepSize += contentTypeNode.deepSize();
        }
        return deepSize;
    }

    public boolean hasChildren()
    {
        return !contentTypeTreeNodes.isEmpty();
    }

    public List<ContentTypeTreeNode> getChildren()
    {
        return contentTypeTreeNodes;
    }

    public ContentTypeTreeNode getFirstChild()
    {
        return this.contentTypeTreeNodes.isEmpty() ? null : this.contentTypeTreeNodes.get( 0 );
    }

    public ContentType getContentType()
    {
        return contentType;
    }

    public ContentTypeTreeNode add( final ContentType contentType )
    {
        final ContentTypeTreeNode contentTypeTreeNode = new ContentTypeTreeNode( this, contentType );
        contentTypeTreeNodes =
            new ImmutableList.Builder<ContentTypeTreeNode>().addAll( contentTypeTreeNodes ).add( contentTypeTreeNode ).build();
        return contentTypeTreeNode;
    }
}