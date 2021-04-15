package com.enonic.xp.impl.server.rest.task;

import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.impl.server.rest.model.VacuumRequestJson;
import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.task.SubmitTaskParams;
import com.enonic.xp.task.TaskId;
import com.enonic.xp.task.TaskResultJson;
import com.enonic.xp.task.TaskService;

public class VacuumCommand
{
    private final TaskService taskService;

    private final VacuumRequestJson params;

    private VacuumCommand( Builder builder )
    {
        this.taskService = builder.taskService;
        this.params = builder.params == null ? new VacuumRequestJson( null, null ) : builder.params;
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
        if ( params.getTasks() != null )
        {
            config.addStrings( "tasks", params.getTasks() );
        }

        final TaskId taskId = taskService.submitTask( SubmitTaskParams.create().
            descriptorKey( DescriptorKey.from( "com.enonic.xp.app.system:vacuum" ) ).
            data( config ).
            build() );

        return new TaskResultJson( taskId );
    }

    public static class Builder
    {
        private TaskService taskService;

        private VacuumRequestJson params;

        public Builder taskService( final TaskService taskService )
        {
            this.taskService = taskService;
            return this;
        }

        public Builder params( final VacuumRequestJson params )
        {
            this.params = params;
            return this;
        }

        public VacuumCommand build()
        {
            return new VacuumCommand( this );
        }
    }
}
