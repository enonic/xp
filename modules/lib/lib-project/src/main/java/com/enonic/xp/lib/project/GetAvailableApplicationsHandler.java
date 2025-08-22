package com.enonic.xp.lib.project;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.project.ProjectName;

public final class GetAvailableApplicationsHandler
    extends BaseProjectHandler
{
    private ProjectName id;

    @Override
    protected List<String> doExecute()
    {
        return this.projectService.get()
            .getAvailableApplications( this.id )
            .stream()
            .map( ApplicationKey::toString )
            .collect( Collectors.toList() );
    }

    public void setId( final String value )
    {
        this.id = ProjectName.from( Objects.requireNonNull( value, "Project name is required" ) );
    }
}
