package com.enonic.xp.admin.impl.rest.resource.content.json;


import java.util.List;

import com.enonic.xp.admin.impl.rest.resource.content.ResolvedContent.ResolvedDependencyContent;

public class ResolvePublishDependenciesResultJson
{
    private final List<ResolvedDependencyContent> dependenciesContents;

    public ResolvePublishDependenciesResultJson( List<ResolvedDependencyContent> dependenciesContents )
    {
        this.dependenciesContents = dependenciesContents;
    }

    @SuppressWarnings("unused")
    public List<ResolvedDependencyContent> getDependenciesContents()
    {
        return dependenciesContents;
    }
}
