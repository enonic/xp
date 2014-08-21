package com.enonic.wem.portal.internal.base;

import org.restlet.resource.ResourceException;

import com.google.common.base.Joiner;

import com.enonic.wem.api.entity.Workspace;
import com.enonic.wem.api.rendering.RenderingMode;

public abstract class WorkspaceBaseResource
    extends BaseResource
{
    protected RenderingMode mode;

    protected Workspace workspace;

    @Override
    protected void doInit()
        throws ResourceException
    {
        final String modeStr = getAttribute( "mode" );
        this.mode = RenderingMode.from( modeStr );
        if ( this.mode == null )
        {
            throw illegalMode( modeStr );
        }

        final String workspaceStr = getAttribute( "workspace" );
        this.workspace = Workspace.from( workspaceStr );
        if ( this.workspace == null )
        {
            throw invalidWorkspace( workspaceStr );
        }

    }

    private ResourceException invalidWorkspace( final String workspace )
    {
        return notFound( "Illegal workspace [%s]", workspace );
    }

    private ResourceException illegalMode( final String mode )
    {
        final String validModes = Joiner.on( "," ).join( RenderingMode.values() ).toLowerCase();
        return notFound( "Illegal mode [%s]. Should be one of [%s].", mode, validModes );
    }
}
