package com.enonic.xp.impl.server.rest.task;

import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.impl.server.rest.model.CleanUpAuditLogRequestJson;
import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.task.SubmitTaskParams;
import com.enonic.xp.task.TaskId;
import com.enonic.xp.task.TaskResultJson;
import com.enonic.xp.task.TaskService;

public class CleanUpAuditLogCommand
{
    private final TaskService taskService;

    private final CleanUpAuditLogRequestJson params;

    private CleanUpAuditLogCommand( Builder builder )
    {
        this.taskService = builder.taskService;
        this.params = builder.params == null ? new CleanUpAuditLogRequestJson( null ) : builder.params;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public TaskResultJson execute()
    {
        PropertyTree config = new PropertyTree();

        if ( params.getAgeThreshold() != null )
        {
            config.addString( "ageThreshold", params.getAgeThreshold() );
        }

        final TaskId taskId = taskService.submitTask( SubmitTaskParams.create().
            descriptorKey( DescriptorKey.from( "com.enonic.xp.app.system:audit-log-cleanup" ) ).
            data( config ).
            build() );

        return new TaskResultJson( taskId );
    }

    public static class Builder
    {
        private TaskService taskService;

        private CleanUpAuditLogRequestJson params;

        public Builder taskService( final TaskService taskService )
        {
            this.taskService = taskService;
            return this;
        }

        public Builder params( final CleanUpAuditLogRequestJson params )
        {
            this.params = params;
            return this;
        }

        public CleanUpAuditLogCommand build()
        {
            return new CleanUpAuditLogCommand( this );
        }
    }
}
