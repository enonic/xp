package com.enonic.xp.admin.impl.rest.resource.project.json;

import java.util.List;
import java.util.stream.Collectors;

import com.enonic.xp.project.ProjectGraph;

public final class ProjectGraphJson
{
    private final List<ProjectGraphEntryJson> entries;

    public ProjectGraphJson( final ProjectGraph projectGraph )
    {
        this.entries = projectGraph != null ? List.copyOf( projectGraph.stream().
            map( ProjectGraphEntryJson::new ).
            collect( Collectors.toList() ) ) : List.of();
    }

    public List<ProjectGraphEntryJson> getEntries()
    {
        return entries;
    }
}
