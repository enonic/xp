package com.enonic.xp.lib.project;

import com.enonic.xp.lib.project.command.ApplyProjectReadAccessCommand;
import com.enonic.xp.lib.project.mapper.ProjectReadAccessMapper;
import com.enonic.xp.project.ProjectName;
import com.enonic.xp.script.ScriptValue;

public final class ModifyProjectReadAccessHandler
    extends BaseProjectHandler
{
    private ProjectName name;

    private boolean isPublic;

    @Override
    protected ProjectReadAccessMapper doExecute()
    {
        final Boolean result = ApplyProjectReadAccessCommand.create().
            setPublic( this.isPublic ).
            contentService( this.contentService ).
            projectName( this.name ).
            build().
            execute();

        return new ProjectReadAccessMapper( result );
    }

    public void setName( final String value )
    {
        this.name = ProjectName.from( value );
    }

    public void setReadAccess( final ScriptValue value )
    {
        this.isPublic = buildReadAccess( value );
    }
}
