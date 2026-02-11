package com.enonic.xp.impl.scheduler;

import java.util.concurrent.Callable;
import java.util.concurrent.Executor;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.audit.AuditLogService;
import com.enonic.xp.audit.AuditLogUri;
import com.enonic.xp.audit.AuditLogUris;
import com.enonic.xp.audit.LogAuditLogParams;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.scheduler.CreateScheduledJobParams;
import com.enonic.xp.scheduler.CronCalendar;
import com.enonic.xp.scheduler.ModifyScheduledJobParams;
import com.enonic.xp.scheduler.OneTimeCalendar;
import com.enonic.xp.scheduler.ScheduleCalendar;
import com.enonic.xp.scheduler.ScheduleCalendarType;
import com.enonic.xp.scheduler.ScheduledJob;
import com.enonic.xp.scheduler.ScheduledJobName;
import com.enonic.xp.scheduler.SchedulerConstants;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.auth.AuthenticationInfo;

@Component(configurationPid = "com.enonic.xp.scheduler", immediate = true)
public class ScheduleAuditLogSupportImpl
    implements ScheduleAuditLogSupport
{
    private static final String SOURCE_CORE_CONTENT = "com.enonic.xp.core-scheduler";

    private final Executor executor;

    private final AuditLogService auditLogService;

    @Activate
    public ScheduleAuditLogSupportImpl( @Reference final SchedulerConfig config,
                                        @Reference(service = ScheduleAuditLogExecutor.class) final Executor executor,
                                        final @Reference AuditLogService auditLogService )
    {
        this.executor = config.auditlogEnabled() ? executor : c -> {
        };
        this.auditLogService = auditLogService;
    }

    @Override
    public void create( final CreateScheduledJobParams params, final ScheduledJob job )
    {
        final Context context = scheduleContext();

        executor.execute( () -> doCreate( params, job, context ) );
    }

    private void doCreate( final CreateScheduledJobParams params, final ScheduledJob job, final Context rootContext )
    {
        final PropertyTree data = new PropertyTree();
        final PropertySet paramsSet = data.addSet( "params" );
        final PropertySet resultSet = data.addSet( "result" );

        addParams( paramsSet, params );
        addResult( resultSet, job );

        log( "system.job.create", data, job.getName(), rootContext );
    }

    @Override
    public void modify( ModifyScheduledJobParams params, ScheduledJob job )
    {
        final Context context = scheduleContext();

        executor.execute( () -> doUpdate( params, job, context ) );
    }

    private void doUpdate( final ModifyScheduledJobParams params, final ScheduledJob job, final Context rootContext )
    {
        final PropertyTree data = new PropertyTree();
        final PropertySet paramsSet = data.addSet( "params" );
        final PropertySet resultSet = data.addSet( "result" );

        addParams( paramsSet, params, rootContext );
        addResult( resultSet, job );

        log( "system.job.update", data, job.getName(), rootContext );
    }

    @Override
    public void delete( final ScheduledJobName name, final boolean result )
    {
        final Context context = scheduleContext();

        executor.execute( () -> doDelete( name, result, context ) );
    }

    private void doDelete( final ScheduledJobName name, final boolean result, final Context rootContext )
    {
        final PropertyTree data = new PropertyTree();
        final PropertySet paramsSet = data.addSet( "params" );
        final PropertySet resultSet = data.addSet( "result" );

        paramsSet.addString( "name", name.getValue() );
        resultSet.addString( "name", name.getValue() );
        resultSet.addBoolean( "value", result );

        log( "system.job.delete", data, name, rootContext );
    }

    private void addParams( final PropertySet targetSet, final CreateScheduledJobParams params )
    {
        targetSet.setString( "name", params.getName().getValue() );
        targetSet.setString( ScheduledJobPropertyNames.DESCRIPTION, params.getDescription() );
        targetSet.setString( ScheduledJobPropertyNames.DESCRIPTOR, params.getDescriptor().toString() );
        targetSet.setSet( ScheduledJobPropertyNames.CONFIG, params.getConfig().getRoot().copy( targetSet.getTree() ) );
        targetSet.setBoolean( ScheduledJobPropertyNames.ENABLED, params.isEnabled() );

        if ( params.getUser() != null )
        {
            targetSet.setString( ScheduledJobPropertyNames.USER, params.getUser().toString() );
        }

        addCalendar( targetSet, params.getCalendar() );
    }

    private void addParams( final PropertySet targetSet, final ModifyScheduledJobParams params, final Context rootContext )
    {
        targetSet.setString( "name", params.getName().getValue() );

        final PrincipalKey modifier =
            rootContext.getAuthInfo().getUser() != null ? rootContext.getAuthInfo().getUser().getKey() : PrincipalKey.ofAnonymous();

        targetSet.setString( "modifier", modifier.toString() );
    }

    private void addResult( final PropertySet targetSet, final ScheduledJob job )
    {
        targetSet.setString( "name", job.getName().getValue() );
        targetSet.setString( ScheduledJobPropertyNames.DESCRIPTION, job.getDescription() );
        targetSet.setString( ScheduledJobPropertyNames.DESCRIPTOR, job.getDescriptor().toString() );
        targetSet.setSet( ScheduledJobPropertyNames.CONFIG, job.getConfig().getRoot().copy( targetSet.getTree() ) );
        targetSet.setBoolean( ScheduledJobPropertyNames.ENABLED, job.isEnabled() );

        if ( job.getUser() != null )
        {
            targetSet.setString( ScheduledJobPropertyNames.USER, job.getUser().toString() );
        }

        if ( job.getCreator() != null )
        {
            targetSet.setString( ScheduledJobPropertyNames.CREATOR, job.getCreator().toString() );
        }
        targetSet.setString( ScheduledJobPropertyNames.MODIFIER, job.getModifier().toString() );
        targetSet.setInstant( ScheduledJobPropertyNames.CREATED_TIME, job.getCreatedTime() );
        targetSet.setInstant( ScheduledJobPropertyNames.MODIFIED_TIME, job.getModifiedTime() );

        addCalendar( targetSet, job.getCalendar() );
    }

    private void addCalendar( final PropertySet targetSet, final ScheduleCalendar calendar )
    {
        final PropertySet calendarSet = targetSet.getTree().newSet();

        switch ( calendar.getType() )
        {
            case CRON:
                final CronCalendar cronCalendar = ( (CronCalendar) calendar );
                calendarSet.setString( ScheduledJobPropertyNames.CALENDAR_VALUE, cronCalendar.getCronValue() );
                calendarSet.setString( ScheduledJobPropertyNames.CALENDAR_TIMEZONE, cronCalendar.getTimeZone().getID() );
                calendarSet.setString( ScheduledJobPropertyNames.CALENDAR_TYPE, ScheduleCalendarType.CRON.name() );
                break;

            case ONE_TIME:
                final OneTimeCalendar oneTimeCalendar = ( (OneTimeCalendar) calendar );
                calendarSet.setString( ScheduledJobPropertyNames.CALENDAR_VALUE, oneTimeCalendar.getValue().toString() );
                calendarSet.setString( ScheduledJobPropertyNames.CALENDAR_TYPE, ScheduleCalendarType.ONE_TIME.name() );
                break;

            default:
                throw new IllegalStateException( String.format( "invalid calendar type: '%s'", calendar.getType() ) );
        }
        targetSet.setSet( ScheduledJobPropertyNames.CALENDAR, calendarSet );

    }

    private void log( final String type, final PropertyTree data, final AuditLogUris uris, final Context rootContext )
    {
        final PrincipalKey userPrincipalKey =
            rootContext.getAuthInfo().getUser() != null ? rootContext.getAuthInfo().getUser().getKey() : PrincipalKey.ofAnonymous();

        final LogAuditLogParams logParams = LogAuditLogParams.create().
            type( type ).
            source( SOURCE_CORE_CONTENT ).
            data( data ).
            objectUris( uris ).
            user( userPrincipalKey ).
            build();

        runAsAuditLog( () -> auditLogService.log( logParams ), rootContext );
    }

    private void log( final String type, final PropertyTree data, final ScheduledJobName name, final Context rootContext )
    {
        log( type, data, AuditLogUris.from( createAuditLogUri( name, rootContext ) ), rootContext );
    }

    private AuditLogUri createAuditLogUri( final ScheduledJobName name, final Context rootContext )
    {
        return AuditLogUri.from( name.getValue() );
    }

    private Context scheduleContext()
    {
        return ContextBuilder.copyOf( ContextAccessor.current() ).
            repositoryId( SchedulerConstants.SCHEDULER_REPO_ID ).
            branch( SchedulerConstants.SCHEDULER_BRANCH ).
            build();
    }

    private <T> T runAsAuditLog( final Callable<T> callable, final Context rootContext )
    {
        return ContextBuilder.from( rootContext ).
            authInfo( AuthenticationInfo.copyOf( rootContext.getAuthInfo() ).
                principals( RoleKeys.AUDIT_LOG ).build() ).
            build().
            callWith( callable );
    }

}
