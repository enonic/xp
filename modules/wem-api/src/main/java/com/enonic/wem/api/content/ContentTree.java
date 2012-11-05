package com.enonic.wem.api.content;


import java.util.LinkedHashMap;

public class ContentTree
{
    private LinkedHashMap<String, ContentBranch> contentBranchByName = new LinkedHashMap<String, ContentBranch>();

    private ContentTree()
    {

    }

    public int size()
    {
        return contentBranchByName.size();
    }

    public int deepSize()
    {
        int deepSize = size();
        for ( ContentBranch contentBranch : contentBranchByName.values() )
        {
            deepSize += contentBranch.deepSize();
        }
        return deepSize;
    }

    public Iterable<ContentBranch> getBranches()
    {
        return contentBranchByName.values();
    }

    public static Builder newContentTree()
    {
        return new Builder();
    }

    @Override
    public String toString()
    {
        final StringBuilder s = new StringBuilder();
        s.append( "/" ).append( "\n" );
        for ( ContentBranch branch : contentBranchByName.values() )
        {
            s.append( "-> " );
            s.append( branch.toString( 1 ) );
        }
        return s.toString();
    }

    public static class Builder
    {
        ContentTree contentTree = new ContentTree();

        public Builder addBranch( ContentBranch branch )
        {
            contentTree.contentBranchByName.put( branch.getName(), branch );
            return this;
        }

        public ContentTree build()
        {
            return contentTree;
        }
    }
}
