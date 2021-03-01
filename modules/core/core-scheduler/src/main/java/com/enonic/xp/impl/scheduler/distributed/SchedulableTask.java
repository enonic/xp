package com.enonic.xp.impl.scheduler.distributed;

import java.io.Serializable;
import java.time.Instant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.hazelcast.scheduledexecutor.NamedTask;

import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.core.internal.osgi.OsgiSupport;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.impl.scheduler.UpdateLastRunCommand;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.scheduler.ScheduleCalendar;
import com.enonic.xp.scheduler.ScheduledJob;
import com.enonic.xp.scheduler.SchedulerName;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.User;
import com.enonic.xp.security.auth.AuthenticationInfo;
import com.enonic.xp.task.SubmitTaskParams;
import com.enonic.xp.task.TaskService;

public final class SchedulableTask
    implements NamedTask, Runnable, Serializable
{
    private static final long serialVersionUID = 0;

    private static final Logger LOG = LoggerFactory.getLogger( SchedulableTask.class );

    private final ScheduledJob job;

    private SchedulableTask( final Builder builder )
    {
        this.job = builder.job;
    }

    public static Builder create()
    {
        return new Builder();
    }

    @Override
    public String getName()
    {
        return job.getName().getValue();
    }

    public ScheduledJob getJob()
    {
        return job;
    }

    private static Context adminContext()
    {
        return ContextBuilder.from( ContextAccessor.current() ).authInfo( AuthenticationInfo.create().
            principals( RoleKeys.ADMIN ).
            user( User.create().
                key( PrincipalKey.ofSuperUser() ).
                login( PrincipalKey.ofSuperUser().getId() ).
                build() ).
            build() ).
            build();
    }

    @Override
    public void run()
    {
        try
        {
            OsgiSupport.withService( TaskService.class, taskService -> taskService.submitTask( SubmitTaskParams.create().
                descriptorKey( job.getDescriptor() ).
                data( job.getPayload() ).
                build() ) );

            adminContext().runWith( () -> OsgiSupport.withService( NodeService.class, nodeService -> UpdateLastRunCommand.create().
                nodeService( nodeService ).
                name( job.getName() ).
                lastRun( Instant.now() ).
                build().
                execute() ) );

        }
        catch ( Exception e )
        {
            LOG.warn( "Error while running job [{}]", this.job.getName(), e );
        }
        catch ( Throwable t )
        {
            LOG.error( "Error while running job [{}], no further attempts will be made", this.job.getName(), t );
            throw t;
        }

    }

    private Object writeReplace()
    {
        return new SerializedForm( this.job );
    }

    private static class SerializedForm
        implements Serializable
    {
        private static final long serialVersionUID = 0;

        private final String name;

        private final String description;

        private final ScheduleCalendar calendar;

        private final boolean enabled;

        private final String descriptor;

        private final PropertyTree payload;

        private final String user;

        private final String author;

        private final Instant lastRun;

        SerializedForm( ScheduledJob job )
        {
            this.name = job.getName().getValue();
            this.description = job.getDescription();
            this.calendar = job.getCalendar();
            this.enabled = job.isEnabled();
            this.descriptor = job.getDescriptor() != null ? job.getDescriptor().toString() : null;
            this.payload = job.getPayload();
            this.user = job.getUser() != null ? job.getUser().toString() : null;
            this.author = job.getAuthor() != null ? job.getAuthor().toString() : null;
            this.lastRun = job.getLastRun();
        }

        private Object readResolve()
        {
            return SchedulableTask.create().
                job( ScheduledJob.create().
                    name( SchedulerName.from( name ) ).
                    description( description ).
                    calendar( calendar ).
                    enabled( enabled ).
                    descriptor( descriptor != null ? DescriptorKey.from( descriptor ) : null ).
                    payload( payload ).
                    user( user != null ? PrincipalKey.from( user ) : null ).
                    author( author != null ? PrincipalKey.from( author ) : null ).
                    lastRun( lastRun ).
                    build() ).
                build();
        }
    }

    public static class Builder
    {
        private ScheduledJob job;

        public Builder job( final ScheduledJob job )
        {
            this.job = job;
            return this;
        }

        private void validate()
        {
            Preconditions.checkNotNull( job, "job must be set." );
        }

        public SchedulableTask build()
        {
            validate();
            return new SchedulableTask( this );
        }
    }
}
