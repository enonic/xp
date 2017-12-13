package com.enonic.xp.admin.impl.rest.resource.content.task;

import com.enonic.xp.admin.impl.rest.resource.content.json.TaskResultJson;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.security.auth.AuthenticationInfo;
import com.enonic.xp.task.RunnableTask;
import com.enonic.xp.task.TaskId;
import com.enonic.xp.task.TaskService;

public abstract class CommonRunnableTask
    implements RunnableTask
{
    private String description;

    private TaskService taskService;

    protected ContentService contentService;

    CommonRunnableTask( Builder builder )
    {
        this.description = builder.description;
        this.taskService = builder.taskService;
        this.contentService = builder.contentService;
    }

    public TaskResultJson createTaskResult()
    {
        final TaskId taskId = taskService.submitTask( this, this.description );
        return new TaskResultJson( taskId );
    }

    public static abstract class Builder
    {

        private String description;

        private TaskService taskService;

        private ContentService contentService;

        private AuthenticationInfo authInfo;

        protected Builder()
        {
        }

        public Builder description( String description )
        {
            this.description = description;
            return this;
        }

        public Builder taskService( TaskService taskService )
        {
            this.taskService = taskService;
            return this;
        }

        public Builder contentService( ContentService contentService )
        {
            this.contentService = contentService;
            return this;
        }

        public abstract CommonRunnableTask build();
    }
}
