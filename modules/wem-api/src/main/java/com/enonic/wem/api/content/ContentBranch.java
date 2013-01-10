package com.enonic.wem.api.content;


import java.util.LinkedHashMap;

import org.apache.commons.lang.StringUtils;

import com.google.common.base.Preconditions;

public class ContentBranch
{
    private Content parent;

    private LinkedHashMap<String, ContentBranch> children = new LinkedHashMap<String, ContentBranch>();

    private ContentBranch()
    {
    }

    public String getName()
    {
        return parent.getName();
    }

    public Content getParent()
    {
        return parent;
    }

    public int size()
    {
        return children.size();
    }

    public int deepSize()
    {
        int deepSize = size();
        for ( ContentBranch contentBranch : children.values() )
        {
            deepSize += contentBranch.deepSize();
        }
        return deepSize;
    }

    public boolean hasChildren()
    {
        return !children.isEmpty();
    }

    public Iterable<ContentBranch> getChildren()
    {
        return children.values();
    }

    @Override
    public String toString()
    {
        return toString( 0 );
    }

    String toString( final int indents )
    {
        final StringBuilder s = new StringBuilder();
        s.append( getName() ).append( "\n" );
        for ( ContentBranch branch : children.values() )
        {
            s.append( StringUtils.leftPad( " ", indents ) );
            s.append( " -> " ).append( branch.toString( indents + 2 ) );
        }
        return s.toString();
    }

    public static Builder newContentBranch()
    {
        return new Builder();
    }

    public static class Builder
    {
        private final ContentBranch branch = new ContentBranch();

        public Builder parent( final Content parent )
        {
            branch.parent = parent;
            return this;
        }

        public Builder addChild( final Content child )
        {
            Preconditions.checkNotNull( branch.parent, "Parent must be set before adding child" );
            final ContentPath childPath = child.getPath();
            final ContentPath parentPath = branch.parent.getPath();
            Preconditions.checkArgument( childPath.isChildOf( parentPath ), "child [%s] is not rightful child of parent: %s", childPath,
                                         parentPath );
            ContentBranch childBranch = newContentBranch().parent( child ).build();
            branch.children.put( child.getName(), childBranch );
            return this;
        }

        public Builder addChild( final ContentBranch child )
        {
            Preconditions.checkNotNull( branch.parent, "Parent must be set before adding child" );
            final ContentPath childPath = child.getParent().getPath();
            final ContentPath parentPath = branch.parent.getPath();
            Preconditions.checkArgument( childPath.isChildOf( parentPath ), "child [%s] is not rightful child of parent: %s", childPath,
                                         parentPath );
            branch.children.put( child.getName(), child );
            return this;
        }

        public ContentBranch build()
        {
            return branch;
        }

    }

}
