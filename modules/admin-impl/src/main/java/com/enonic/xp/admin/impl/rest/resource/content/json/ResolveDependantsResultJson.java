package com.enonic.xp.admin.impl.rest.resource.content.json;


import java.util.List;

import com.enonic.xp.admin.impl.rest.resource.content.ResolvedContent;

public class ResolveDependantsResultJson
{
    private final List<ResolvedContent> dependantContents;

    public ResolveDependantsResultJson( final List<ResolvedContent> dependantContents )
    {
        this.dependantContents = dependantContents;
    }

    @SuppressWarnings("unused")
    public List<ResolvedContent> getDependantContents()
    {
        return dependantContents;
    }
}
