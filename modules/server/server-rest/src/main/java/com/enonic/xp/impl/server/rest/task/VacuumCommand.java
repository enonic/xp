package com.enonic.xp.impl.server.rest.task;

import java.util.List;

import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.task.SubmitTaskParams;
import com.enonic.xp.task.TaskId;
import com.enonic.xp.task.TaskService;

public class VacuumCommand
{
    private static final DescriptorKey TASK_DESCRIPTOR_KEY = DescriptorKey.from( "com.enonic.xp.app.system:vacuum" );

    private final TaskService taskService;

    private final String ageThreshold;

    private final List<String> tasks;

    private VacuumCommand( Builder builder )
    {
        this.taskService = builder.taskService;
        this.ageThreshold = builder.ageThreshold;
        this.tasks = builder.tasks;
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
        if ( tasks != null )
        {
            config.addStrings( "tasks", tasks );
        }

        return taskService.submitTask( SubmitTaskParams.create().descriptorKey( TASK_DESCRIPTOR_KEY ).data( config ).build() );
    }

    public static class Builder
    {
        private TaskService taskService;

        private String ageThreshold;

        private List<String> tasks;

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

        public Builder tasks( final List<String> tasks )
        {
            this.tasks = tasks;
            return this;
        }

        public VacuumCommand build()
        {
            return new VacuumCommand( this );
        }
    }
}
