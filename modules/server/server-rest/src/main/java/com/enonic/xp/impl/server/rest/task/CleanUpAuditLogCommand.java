package com.enonic.xp.impl.server.rest.task;

import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.task.SubmitTaskParams;
import com.enonic.xp.task.TaskId;
import com.enonic.xp.task.TaskService;

public class CleanUpAuditLogCommand
{
    private static final DescriptorKey TASK_DESCRIPTOR_KEY = DescriptorKey.from( "com.enonic.xp.app.system:audit-log-cleanup" );

    private final TaskService taskService;

    private final String ageThreshold;

    private CleanUpAuditLogCommand( Builder builder )
    {
        this.taskService = builder.taskService;
        this.ageThreshold = builder.ageThreshold;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public TaskId execute()
    {
        PropertyTree config = new PropertyTree();

        if ( ageThreshold != null )
        {
            config.addString( "ageThreshold", ageThreshold );
        }

        return taskService.submitTask( SubmitTaskParams.create().descriptorKey( TASK_DESCRIPTOR_KEY ).data( config ).build() );
    }

    public static class Builder
    {
        private TaskService taskService;

        private String ageThreshold;

        public Builder taskService( final TaskService taskService )
        {
            this.taskService = taskService;
            return this;
        }

        public Builder ageThreshold( final String ageThreshold )
        {
            this.ageThreshold = ageThreshold;
            return this;
        }

        public CleanUpAuditLogCommand build()
        {
            return new CleanUpAuditLogCommand( this );
        }
    }
}
