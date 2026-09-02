package com.enonic.xp.app.system;

import java.util.List;

import com.enonic.xp.app.system.json.SystemDumpResultJson;
import com.enonic.xp.app.system.listener.SystemDumpListenerImpl;
import com.enonic.xp.dump.DumpService;
import com.enonic.xp.dump.SystemDumpParams;
import com.enonic.xp.dump.SystemDumpResult;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.repository.RepositoryIds;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;
import com.enonic.xp.task.ProgressReportParams;
import com.enonic.xp.task.ProgressReporter;
import com.enonic.xp.task.TaskId;
import com.enonic.xp.task.TaskProgressReporterContext;
import com.enonic.xp.task.TaskService;

/**
 * The {@code com.enonic.xp.app.system:dump} task: dumps all or the named repositories to {@code $XP_HOME/data/dump/<name>}.
 * A cluster task - the data directory is shared between the nodes of a cluster.
 */
public class DumpTaskHandler
    implements ScriptBean
{
    private DumpService dumpService;

    private TaskService taskService;

    private String name;

    private boolean includeVersions;

    private Integer maxAge;

    private Integer maxVersions;

    private List<String> repositories;

    private TaskId taskId;

    public void setName( final String name )
    {
        this.name = name;
    }

    public void setIncludeVersions( final boolean includeVersions )
    {
        this.includeVersions = includeVersions;
    }

    public void setMaxAge( final Integer maxAge )
    {
        this.maxAge = maxAge;
    }

    public void setMaxVersions( final Integer maxVersions )
    {
        this.maxVersions = maxVersions;
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

        final SystemDumpResult result = dumpService.dump( SystemDumpParams.create()
                                                              .dumpName( name )
                                                              .includeBinaries( true )
                                                              .includeVersions( includeVersions )
                                                              .maxAge( maxAge )
                                                              .maxVersions( maxVersions )
                                                              .repositories( repositoryIds( repositories ) )
                                                              .listener( new SystemDumpListenerImpl( progressReporter ) )
                                                              .build() );

        progressReporter.progress( ProgressReportParams.create( SystemDumpResultJson.from( result ).toString() ).build() );
    }

    static RepositoryIds repositoryIds( final List<String> repositories )
    {
        return repositories == null || repositories.isEmpty()
            ? RepositoryIds.empty()
            : repositories.stream().map( RepositoryId::from ).collect( RepositoryIds.collector() );
    }

    @Override
    public void initialize( final BeanContext context )
    {
        this.dumpService = context.getService( DumpService.class ).get();
        this.taskService = context.getService( TaskService.class ).get();
    }
}
