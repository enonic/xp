package com.enonic.xp.admin.impl.rest.resource.content.json;

import java.util.Set;

public class PublishContentJson
{
    private Set<String> ids;

    private Set<String> excludedIds;

    private Set<String> excludeChildrenIds;

    private PublishScheduleJson schedule;

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
}