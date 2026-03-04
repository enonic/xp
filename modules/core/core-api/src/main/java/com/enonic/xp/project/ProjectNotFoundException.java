package com.enonic.xp.project;

import java.text.MessageFormat;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.exception.NotFoundException;

@PublicApi
public final class ProjectNotFoundException
    extends NotFoundException
{
    public ProjectNotFoundException( final ProjectName projectName )
    {
        super( MessageFormat.format( "Project [{0}] was not found", projectName.toString() ) );
    }
}
