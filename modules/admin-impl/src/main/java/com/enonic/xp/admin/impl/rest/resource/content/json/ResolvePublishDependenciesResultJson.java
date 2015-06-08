package com.enonic.xp.admin.impl.rest.resource.content.json;


import java.util.List;

import com.enonic.xp.admin.impl.rest.resource.content.ResolvedContent;
import com.enonic.xp.admin.impl.rest.resource.content.ResolvedContent.ResolvedRequestedContent;

public class ResolvePublishDependenciesResultJson
{
    private final List<ResolvedRequestedContent> pushRequestedContents;

    public ResolvePublishDependenciesResultJson( final List<ResolvedContent.ResolvedRequestedContent> pushRequestedContents )
    {
        this.pushRequestedContents = pushRequestedContents;
    }

    @SuppressWarnings("unused")
    public List<ResolvedRequestedContent> getPushRequestedContents()
    {
        return pushRequestedContents;
    }
}