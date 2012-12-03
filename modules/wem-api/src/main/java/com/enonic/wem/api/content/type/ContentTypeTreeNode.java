package com.enonic.wem.api.content.type;

import java.util.List;

import com.google.common.collect.ImmutableList;

public final class ContentTypeTreeNode
{
    private final ContentTypeTreeNode parent;

    private final ContentType contentType;

    private ImmutableList<ContentTypeTreeNode> subTypes;

    ContentTypeTreeNode( final ContentTypeTreeNode parent, final ContentType contentType, final ContentTypeTreeNode... children )
    {
        this.parent = parent;
        this.contentType = contentType;
        this.subTypes = ImmutableList.copyOf( children );
    }

    public ContentTypeTreeNode parent()
    {
        return parent;
    }

    public int size()
    {
        return subTypes.size();
    }

    public int deepSize()
    {
        int deepSize = size();
        for ( ContentTypeTreeNode contentTypeNode : subTypes )
        {
            deepSize += contentTypeNode.deepSize();
        }
        return deepSize;
    }

    public boolean hasChildren()
    {
        return !subTypes.isEmpty();
    }

    public List<ContentTypeTreeNode> getChildren()
    {
        return subTypes;
    }

    public ContentTypeTreeNode getFirstChild()
    {
        return this.subTypes.isEmpty() ? null : this.subTypes.get( 0 );
    }

    public ContentType getContentType()
    {
        return contentType;
    }

    public ContentTypeTreeNode add( final ContentType subType )
    {
        final ContentTypeTreeNode newSubType = new ContentTypeTreeNode( this, subType );
        subTypes = new ImmutableList.Builder<ContentTypeTreeNode>().addAll( subTypes ).add( newSubType ).build();
        return newSubType;
    }
}