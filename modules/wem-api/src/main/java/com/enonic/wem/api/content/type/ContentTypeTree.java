package com.enonic.wem.api.content.type;


import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

public final class ContentTypeTree
{
    private final ImmutableList<ContentTypeTreeNode> rootTypes;

    public ContentTypeTree( final Iterable<ContentType> contentTypes )
    {
        final List<ContentTypeTreeNode> rootTypeNodes = Lists.newArrayList();
        for ( ContentType contentType : contentTypes )
        {
            rootTypeNodes.add( new ContentTypeTreeNode( null, contentType ) );
        }
        this.rootTypes = ImmutableList.copyOf( rootTypeNodes );
    }

    public int size()
    {
        return rootTypes.size();
    }

    public int deepSize()
    {
        int deepSize = size();
        for ( ContentTypeTreeNode contentTypeNode : rootTypes )
        {
            deepSize += contentTypeNode.deepSize();
        }
        return deepSize;
    }

    public boolean hasChildren()
    {
        return !rootTypes.isEmpty();
    }

    public List<ContentTypeTreeNode> getChildren()
    {
        return rootTypes;
    }

    public ContentTypeTreeNode getFirstChild()
    {
        return this.rootTypes.isEmpty() ? null : this.rootTypes.get( 0 );
    }

}
