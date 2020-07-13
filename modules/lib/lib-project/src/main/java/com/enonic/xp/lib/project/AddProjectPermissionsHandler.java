package com.enonic.xp.lib.project;

import java.util.Set;

import com.enonic.xp.project.ProjectPermissions;

public final class AddProjectPermissionsHandler
    extends ModifyProjectPermissionsHandler
{
    @Override
    protected ProjectPermissions merge( final ProjectPermissions permissionsBeforeUpdate, final ProjectPermissions paramPermissions )
    {
        final ProjectPermissions.Builder builder = ProjectPermissions.create();

        Set.of( permissionsBeforeUpdate, paramPermissions ).forEach( permissions -> {
            permissions.getOwner().forEach( builder::addOwner );
            permissions.getEditor().forEach( builder::addEditor );
            permissions.getAuthor().forEach( builder::addAuthor );
            permissions.getContributor().forEach( builder::addContributor );
            permissions.getViewer().forEach( builder::addViewer );
        } );

        return builder.build();
    }

}
