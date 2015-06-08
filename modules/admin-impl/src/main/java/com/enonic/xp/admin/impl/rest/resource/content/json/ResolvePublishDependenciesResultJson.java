package com.enonic.xp.admin.impl.rest.resource.content.json;


import java.util.List;

import com.enonic.xp.admin.impl.rest.resource.content.ResolvedContent;

public class ResolvePublishDependenciesResultJson
{
    private final List<ResolvedContent> dependantContents;

    private final List<ResolvedContent> childrenContents;

    public ResolvePublishDependenciesResultJson( final List<ResolvedContent> dependantContents,
                                                 final List<ResolvedContent> childrenContents )
    {
        this.dependantContents = dependantContents;
        this.childrenContents = childrenContents;
    }

    @SuppressWarnings("unused")
    public List<ResolvedContent> getDependantContents()
    {
        return dependantContents;
    }

    @SuppressWarnings("unused")
    public List<ResolvedContent> getChildrenContents()
    {
        return childrenContents;
    }
}
