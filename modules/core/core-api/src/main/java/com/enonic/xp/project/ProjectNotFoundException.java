package com.enonic.xp.project;

import java.text.MessageFormat;

import org.jspecify.annotations.NullMarked;

import com.enonic.xp.exception.NotFoundException;

@NullMarked
public final class ProjectNotFoundException
    extends NotFoundException
{
    public ProjectNotFoundException( final ProjectName projectName )
    {
        super( MessageFormat.format( "Project [{0}] was not found", projectName.toString() ) );
    }
}
