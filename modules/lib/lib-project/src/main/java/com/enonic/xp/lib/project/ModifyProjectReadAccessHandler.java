package com.enonic.xp.lib.project;

import com.enonic.xp.lib.project.command.ApplyProjectReadAccessCommand;
import com.enonic.xp.lib.project.mapper.ProjectReadAccessMapper;
import com.enonic.xp.project.ProjectName;
import com.enonic.xp.script.ScriptValue;

public final class ModifyProjectReadAccessHandler
    extends BaseProjectHandler
{
    private ProjectName id;

    private boolean isPublic;

    @Override
    protected ProjectReadAccessMapper doExecute()
    {
        final Boolean result = ApplyProjectReadAccessCommand.create()
            .setPublic( this.isPublic )
            .contentService( this.contentService.get() )
            .projectName( this.id )
            .build()
            .execute();

        return new ProjectReadAccessMapper( result );
    }

    public void setId( final String value )
    {
        this.id = ProjectName.from( value );
    }

    public void setReadAccess( final ScriptValue value )
    {
        this.isPublic = buildReadAccess( value );
    }
}
