package com.enonic.xp.admin.impl.rest.resource.content.json;

import java.util.Set;
import java.util.stream.Collectors;

public class PublishContentJson
{
    private Set<String> ids;

    private Set<String> excludedIds;

    private Set<String> excludeChildrenIds;

    private PublishScheduleJson schedule;

    private String message;

    private Set<PublishDependencyExcludeState> excludeStates =
        Set.of( PublishDependencyExcludeState.INVALID, PublishDependencyExcludeState.IN_PROGRESS );

    public Set<String> getExcludeChildrenIds()
    {
        return excludeChildrenIds;
    }

    public void setExcludeChildrenIds( final Set<String> excludeChildrenIds )
    {
        this.excludeChildrenIds = excludeChildrenIds;
    }

    public Set<String> getIds()
    {
        return ids;
    }

    public void setIds( final Set<String> ids )
    {
        this.ids = ids;
    }

    public PublishScheduleJson getSchedule()
    {
        return schedule;
    }

    public void setSchedule( final PublishScheduleJson schedule )
    {
        this.schedule = schedule;
    }

    public Set<String> getExcludedIds()
    {
        return excludedIds;
    }

    public void setExcludedIds( final Set<String> excludedIds )
    {
        this.excludedIds = excludedIds;
    }

    public String getMessage()
    {
        return message;
    }

    public void setMessage( final String message )
    {
        this.message = message;
    }

    public Set<PublishDependencyExcludeState> getExcludeStates()
    {
        return excludeStates;
    }

    public void setExcludeStates( final Set<String> excludeStates )
    {
        this.excludeStates = excludeStates != null ? excludeStates.stream().
            map( PublishDependencyExcludeState::valueOf ).
            collect( Collectors.toSet() ) : Set.of();
    }
}