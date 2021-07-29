package com.enonic.xp.admin.impl.rest.resource.content;

import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.admin.impl.rest.resource.content.json.ArchivedContainerJson;
import com.enonic.xp.admin.impl.rest.resource.content.json.RestoreContentJson;
import com.enonic.xp.admin.impl.rest.resource.content.task.ArchiveRunnableTask;
import com.enonic.xp.admin.impl.rest.resource.content.task.RestoreRunnableTask;
import com.enonic.xp.archive.ListContentsParams;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.jaxrs.JaxRsComponent;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.task.TaskResultJson;
import com.enonic.xp.task.TaskService;

import static com.enonic.xp.admin.impl.rest.resource.ResourceConstants.CMS_PATH;
import static com.enonic.xp.admin.impl.rest.resource.ResourceConstants.REST_ROOT;
import static com.google.common.base.Strings.isNullOrEmpty;

@SuppressWarnings("UnusedDeclaration")
@Path(REST_ROOT + "{content:(content|" + CMS_PATH + "/content)}/archive")
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed({RoleKeys.ADMIN_LOGIN_ID, RoleKeys.ADMIN_ID})
@Component(immediate = true, property = "group=admin")
public final class ArchiveResource
    implements JaxRsComponent
{
    private static final Logger LOG = LoggerFactory.getLogger( ArchiveResource.class );

    private ContentService contentService;

    private TaskService taskService;

    @POST
    @Path("archive")
    public TaskResultJson archive( final ArchiveContentJson params )
    {
        return ArchiveRunnableTask.create()
            .params( params )
            .description( "Archive content" )
            .taskService( taskService )
            .contentService( contentService )
            .build()
            .createTaskResult();
    }

    @POST
    @Path("restore")
    public TaskResultJson restore( final RestoreContentJson params )
    {
        return RestoreRunnableTask.create()
            .params( params )
            .description( "Restore content" )
            .taskService( taskService )
            .contentService( contentService )
            .build()
            .createTaskResult();
    }

    @GET
    @Path("list")
    public List<ArchivedContainerJson> list( @QueryParam("parentId") @DefaultValue("") final String parentId )
    {
        final ContentId parentContentId = isNullOrEmpty( parentId ) ? null : ContentId.from( parentId );

        return contentService.listArchived( ListContentsParams.create().parent( parentContentId ).build() )
            .stream()
            .map( ArchivedContainerJson::new )
            .collect( Collectors.toList() );
    }


    @Reference
    public void setContentService( final ContentService contentService )
    {
        this.contentService = contentService;
    }

    @Reference
    public void setTaskService( final TaskService taskService )
    {
        this.taskService = taskService;
    }
}
