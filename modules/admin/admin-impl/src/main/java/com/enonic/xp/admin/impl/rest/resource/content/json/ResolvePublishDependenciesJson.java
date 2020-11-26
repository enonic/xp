package com.enonic.xp.admin.impl.rest.resource.content.json;

import java.util.Set;
import java.util.stream.Collectors;

public class ResolvePublishDependenciesJson
{
    private Set<String> ids;

    private Set<String> excludedIds;

    private Set<String> excludeChildrenIds;

    private Set<PublishDependencyExcludeState> excludeStates =
        Set.of( PublishDependencyExcludeState.INVALID, PublishDependencyExcludeState.IN_PROGRESS );

    public Set<String> getIds()
    {
        return ids;
    }

    @SuppressWarnings("unused")
    public void setIds( final Set<String> ids )
    {
        this.ids = ids;
    }

    public Set<String> getExcludedIds()
    {
        return excludedIds;
    }

    public void setExcludedIds( final Set<String> excludedIds )
    {
        this.excludedIds = excludedIds;
    }

    public Set<String> getExcludeChildrenIds()
    {
        return excludeChildrenIds;
    }

    @SuppressWarnings("unused")
    public void setExcludeChildrenIds( final Set<String> excludeChildrenIds )
    {
        this.excludeChildrenIds = excludeChildrenIds;
    }

    public Set<PublishDependencyExcludeState> getExcludeStates()
    {
        return excludeStates;
    }

    public void setExcludeStates( final Set<String> excludeStates )
    {
        this.excludeStates = excludeStates != null
            ? excludeStates.stream().map( PublishDependencyExcludeState::valueOf ).collect( Collectors.toSet() )
            : Set.of();
    }
}