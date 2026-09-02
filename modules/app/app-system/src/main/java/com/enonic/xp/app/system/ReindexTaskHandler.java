package com.enonic.xp.app.system;

import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import com.enonic.xp.app.system.listener.ReindexListenerImpl;
import com.enonic.xp.branch.Branch;
import com.enonic.xp.branch.Branches;
import com.enonic.xp.core.internal.json.ObjectMapperHelper;
import com.enonic.xp.index.IndexService;
import com.enonic.xp.index.ReindexParams;
import com.enonic.xp.index.ReindexResult;
import com.enonic.xp.repository.Repository;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.repository.RepositoryService;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;
import com.enonic.xp.task.ProgressReportParams;
import com.enonic.xp.task.ProgressReporter;
import com.enonic.xp.task.TaskId;
import com.enonic.xp.task.TaskProgressReporterContext;
import com.enonic.xp.task.TaskService;

/**
 * The {@code com.enonic.xp.app.system:reindex} task: rebuilds the search index of a repository from storage. Runs as a
 * cluster task, on whichever node picks it up.
 */
public class ReindexTaskHandler
    implements ScriptBean
{
    private static final ObjectMapper MAPPER = ObjectMapperHelper.create();

    private IndexService indexService;

    private RepositoryService repositoryService;

    private TaskService taskService;

    private String repository;

    private List<String> branches;

    private boolean initialize;

    private TaskId taskId;

    public void setRepository( final String repository )
    {
        this.repository = repository;
    }

    public void setBranches( final List<String> branches )
    {
        this.branches = branches;
    }

    public void setInitialize( final boolean initialize )
    {
        this.initialize = initialize;
    }

    public void setTaskId( final String taskId )
    {
        this.taskId = TaskId.from( taskId );
    }

    public void execute()
    {
        TaskUtils.checkAlreadySubmitted( taskService.getTaskInfo( taskId ), taskService.getAllTasks() );

        final RepositoryId repositoryId = RepositoryId.from( repository );
        final Branches reindexBranches;
        if ( branches == null || branches.isEmpty() )
        {
            final Repository repo = repositoryService.get( repositoryId );
            if ( repo == null )
            {
                throw new IllegalArgumentException( "Repository [" + repositoryId + "] not found" );
            }
            reindexBranches = repo.getBranches();
        }
        else
        {
            reindexBranches = branches.stream().map( Branch::from ).collect( Branches.collector() );
        }

        final ProgressReporter progressReporter = TaskProgressReporterContext.current();
        final ReindexResult result = indexService.reindex( ReindexParams.create()
                                                               .repositoryId( repositoryId )
                                                               .setBranches( reindexBranches )
                                                               .initialize( initialize )
                                                               .listener( new ReindexListenerImpl( progressReporter ) )
                                                               .build() );

        final ObjectNode json = MAPPER.createObjectNode();
        json.put( "repositoryId", result.getRepositoryId().toString() );
        json.put( "duration", result.getDuration().toString() );
        json.put( "startTime", result.getStartTime().toString() );
        json.put( "endTime", result.getEndTime().toString() );
        json.put( "numberReindexed", result.getReindexNodes().getSize() );
        json.putPOJO( "branches", result.getBranches().stream().map( Branch::getValue ).toList() );
        try
        {
            progressReporter.progress( ProgressReportParams.create( MAPPER.writeValueAsString( json ) ).build() );
        }
        catch ( JsonProcessingException e )
        {
            throw new RuntimeException( e );
        }
    }

    @Override
    public void initialize( final BeanContext context )
    {
        this.indexService = context.getService( IndexService.class ).get();
        this.repositoryService = context.getService( RepositoryService.class ).get();
        this.taskService = context.getService( TaskService.class ).get();
    }
}
