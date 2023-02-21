package com.enonic.xp.task;

import com.enonic.xp.content.ContentService;

@Deprecated
public abstract class AbstractRunnableTask
    implements RunnableTask
{
    protected final String description;

    protected final TaskService taskService;

    protected ContentService contentService;

    protected AbstractRunnableTask( Builder builder )
    {
        this.description = builder.description;
        this.taskService = builder.taskService;
        this.contentService = builder.contentService;
    }

    public TaskId createTaskResult()
    {
        return taskService.submitLocalTask( SubmitLocalTaskParams.create().runnableTask( this ).description( description ).build() );
    }

    public abstract static class Builder<T extends Builder>
    {

        private String description;

        private TaskService taskService;

        private ContentService contentService;

        public T description( final String description )
        {
            this.description = description;
            return (T) this;
        }

        public T taskService( final TaskService taskService )
        {
            this.taskService = taskService;
            return (T) this;
        }

        public T contentService( final ContentService contentService )
        {
            this.contentService = contentService;
            return (T) this;
        }

        public abstract AbstractRunnableTask build();
    }
}
