package com.enonic.xp.project;

import com.google.common.annotations.Beta;

@Beta
public interface ProjectService
{
    Project create( final CreateProjectParams params );

    Project modify( final ModifyProjectParams params );

    Projects list();

    Project get( final ProjectName projectName );

    void delete( final ProjectName projectName );
}
