package com.enonic.xp.app.system;

import java.util.List;

import com.enonic.xp.app.system.json.SystemLoadResultJson;
import com.enonic.xp.app.system.listener.SystemLoadListenerImpl;
import com.enonic.xp.dump.DumpService;
import com.enonic.xp.dump.SystemLoadParams;
import com.enonic.xp.dump.SystemLoadResult;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;
import com.enonic.xp.task.ProgressReportParams;
import com.enonic.xp.task.ProgressReporter;
import com.enonic.xp.task.TaskId;
import com.enonic.xp.task.TaskProgressReporterContext;
import com.enonic.xp.task.TaskService;

/**
 * The {@code com.enonic.xp.app.system:load} task: loads a dump from {@code $XP_HOME/data/dump/<name>}, replacing the
 * current data of all or the named repositories.
 */
public class LoadTaskHandler
    implements ScriptBean
{
    private DumpService dumpService;

    private TaskService taskService;

    private String name;

    private boolean upgrade;

    private List<String> repositories;

    private TaskId taskId;

    public void setName( final String name )
    {
        this.name = name;
    }

    public void setUpgrade( final boolean upgrade )
    {
        this.upgrade = upgrade;
    }

    public void setRepositories( final List<String> repositories )
    {
        this.repositories = repositories;
    }

    public void setTaskId( final String taskId )
    {
        this.taskId = TaskId.from( taskId );
    }

    public void execute()
    {
        TaskUtils.checkAlreadySubmitted( taskService.getTaskInfo( taskId ), taskService.getAllTasks() );
        final ProgressReporter progressReporter = TaskProgressReporterContext.current();

        final SystemLoadResult result = dumpService.load( SystemLoadParams.create()
                                                              .dumpName( name )
                                                              .upgrade( upgrade )
                                                              .repositories( DumpTaskHandler.repositoryIds( repositories ) )
                                                              .includeVersions( true )
                                                              .listener( new SystemLoadListenerImpl( progressReporter ) )
                                                              .build() );

        progressReporter.progress( ProgressReportParams.create( SystemLoadResultJson.from( result ).toString() ).build() );
    }

    @Override
    public void initialize( final BeanContext context )
    {
        this.dumpService = context.getService( DumpService.class ).get();
        this.taskService = context.getService( TaskService.class ).get();
    }
}
