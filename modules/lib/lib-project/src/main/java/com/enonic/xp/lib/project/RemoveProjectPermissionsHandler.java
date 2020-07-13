package com.enonic.xp.lib.project;

import com.enonic.xp.project.ProjectPermissions;

public final class RemoveProjectPermissionsHandler
    extends ModifyProjectPermissionsHandler
{
    @Override
    protected ProjectPermissions merge( final ProjectPermissions permissionsBeforeUpdate, final ProjectPermissions paramPermissions )
    {
        final ProjectPermissions.Builder builder = ProjectPermissions.create();

        permissionsBeforeUpdate.getOwner().stream().filter( principalKey -> !paramPermissions.getOwner().contains( principalKey ) ).forEach(
            builder::addOwner );
        permissionsBeforeUpdate.getEditor().stream().filter( principalKey -> !paramPermissions.getEditor().contains( principalKey ) ).forEach( builder::addEditor );
        permissionsBeforeUpdate.getAuthor().stream().filter( principalKey -> !paramPermissions.getAuthor().contains( principalKey ) ).forEach( builder::addAuthor );
        permissionsBeforeUpdate.getContributor().stream().filter( principalKey -> !paramPermissions.getContributor().contains( principalKey ) ).forEach( builder::addContributor );
        permissionsBeforeUpdate.getViewer().stream().filter( principalKey -> !paramPermissions.getViewer().contains( principalKey ) ).forEach( builder::addViewer );

        return builder.build();
    }

}
