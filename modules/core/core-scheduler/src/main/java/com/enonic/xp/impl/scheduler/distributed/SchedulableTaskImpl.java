package com.enonic.xp.impl.scheduler.distributed;

import java.io.Serializable;
import java.time.Instant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;

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
import com.enonic.xp.scheduler.ScheduledJobName;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.SecurityService;
import com.enonic.xp.security.User;
import com.enonic.xp.security.auth.AuthenticationInfo;
import com.enonic.xp.security.auth.VerifiedUsernameAuthToken;
import com.enonic.xp.task.SubmitTaskParams;
import com.enonic.xp.task.TaskService;

public final class SchedulableTaskImpl
    implements SchedulableTask
{
    private static final long serialVersionUID = 0;

    private static final Logger LOG = LoggerFactory.getLogger( SchedulableTaskImpl.class );

    private final ScheduledJob job;

    private SchedulableTaskImpl( final Builder builder )
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

    @Override
    public void run()
    {
        try
        {
            taskContext().runWith(
                () -> OsgiSupport.withService( TaskService.class, taskService -> taskService.submitTask( SubmitTaskParams.create().
                    descriptorKey( job.getDescriptor() ).
                    data( job.getConfig() ).
                    build() ) ) );

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

    private Context taskContext()
    {
        if ( job.getUser() == null )
        {
            return ContextBuilder.from( ContextAccessor.current() ).
                authInfo( AuthenticationInfo.unAuthenticated() ).
                build();
        }

        final AuthenticationInfo authInfo = OsgiSupport.withService( SecurityService.class, securityService -> {
            final VerifiedUsernameAuthToken token = new VerifiedUsernameAuthToken();
            token.setIdProvider( job.getUser().getIdProviderKey() );
            token.setUsername( job.getUser().getId() );

            return securityService.authenticate( token );
        } );

        return ContextBuilder.from( ContextAccessor.current() ).
            authInfo( authInfo ).
            build();
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

        private final PropertyTree config;

        private final String user;

        private final String creator;

        private final String modifier;

        private final Instant lastRun;

        private final Instant createdTime;

        private final Instant modifiedTime;

        SerializedForm( ScheduledJob job )
        {
            this.name = job.getName().getValue();
            this.description = job.getDescription();
            this.calendar = job.getCalendar();
            this.enabled = job.isEnabled();
            this.descriptor = job.getDescriptor() != null ? job.getDescriptor().toString() : null;
            this.config = job.getConfig();
            this.user = job.getUser() != null ? job.getUser().toString() : null;
            this.creator = job.getCreator() != null ? job.getCreator().toString() : null;
            this.modifier = job.getModifier() != null ? job.getModifier().toString() : null;
            this.createdTime = job.getCreatedTime();
            this.modifiedTime = job.getModifiedTime();
            this.lastRun = job.getLastRun();
        }

        private Object readResolve()
        {
            return SchedulableTaskImpl.create().
                job( ScheduledJob.create().
                    name( ScheduledJobName.from( name ) ).
                    description( description ).
                    calendar( calendar ).
                    enabled( enabled ).
                    descriptor( descriptor != null ? DescriptorKey.from( descriptor ) : null ).
                    config( config ).
                    user( user != null ? PrincipalKey.from( user ) : null ).
                    creator( creator != null ? PrincipalKey.from( creator ) : null ).
                    modifier( modifier != null ? PrincipalKey.from( modifier ) : null ).
                    createdTime( createdTime ).
                    modifiedTime( modifiedTime ).
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

        public SchedulableTaskImpl build()
        {
            validate();
            return new SchedulableTaskImpl( this );
        }
    }
}
