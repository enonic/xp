package com.enonic.xp.impl.server.rest;

import javax.ws.rs.core.MediaType;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.task.RunnableTask;
import com.enonic.xp.task.TaskId;
import com.enonic.xp.task.TaskService;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ContentResourceTest
    extends ServerRestTestSupport
{
    private ContentService contentService;

    private TaskService taskService;

    @Test
    public void reprocess_skip_children()
        throws Exception
    {
        Content content = createContent( "content-id", ContentPath.from( "/path/to/content" ) );
        Content reprocessedContent = Content.create( content ).displayName( "new name" ).build();
        TaskId taskId = TaskId.from( "task-id" );
        Mockito.when( this.contentService.getByPath( content.getPath() ) ).thenReturn( content );
        Mockito.when( this.contentService.reprocess( content.getId() ) ).thenReturn( reprocessedContent );
        Mockito.when( this.taskService.submitTask( Mockito.any( RunnableTask.class ), Mockito.anyString() ) ).thenReturn( taskId );

        final String result = request().path( "content/reprocess" ).
            entity( readFromFile( "reprocess_params_skip_children.json" ), MediaType.APPLICATION_JSON_TYPE ).
            post().getAsString();

        assertEquals( "{\"taskId\":\"task-id\"}", result );
    }

    @Override
    protected Object getResourceInstance()
    {
        this.contentService = Mockito.mock( ContentService.class );
        this.taskService = Mockito.mock( TaskService.class );

        final ContentResource resource = new ContentResource();
        resource.setContentService( contentService );
        resource.setTaskService( taskService );
        return resource;
    }

    private Content createContent( final String contentId, final ContentPath contentPath )
    {
        return Content.create().
            id( ContentId.from( contentId ) ).
            type( ContentTypeName.folder() ).
            displayName( "Content display name" ).
            name( "content-name" ).
            path( contentPath ).
            build();
    }
}
