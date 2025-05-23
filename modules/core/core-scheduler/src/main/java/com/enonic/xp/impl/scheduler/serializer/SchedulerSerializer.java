package com.enonic.xp.impl.scheduler.serializer;

import java.time.Instant;
import java.util.Optional;
import java.util.TimeZone;
import java.util.function.Supplier;

import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.impl.scheduler.ScheduledJobPropertyNames;
import com.enonic.xp.impl.scheduler.distributed.CronCalendarImpl;
import com.enonic.xp.impl.scheduler.distributed.OneTimeCalendarImpl;
import com.enonic.xp.node.Node;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.scheduler.CreateScheduledJobParams;
import com.enonic.xp.scheduler.CronCalendar;
import com.enonic.xp.scheduler.EditableScheduledJob;
import com.enonic.xp.scheduler.ModifyScheduledJobParams;
import com.enonic.xp.scheduler.OneTimeCalendar;
import com.enonic.xp.scheduler.ScheduleCalendar;
import com.enonic.xp.scheduler.ScheduleCalendarType;
import com.enonic.xp.scheduler.ScheduledJob;
import com.enonic.xp.scheduler.ScheduledJobEditor;
import com.enonic.xp.scheduler.ScheduledJobName;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.User;
import com.enonic.xp.task.TaskId;

public class SchedulerSerializer
{

    private SchedulerSerializer()
    {
    }

    public static PropertyTree toCreateNodeData( final CreateScheduledJobParams params )
    {
        final PropertyTree tree = new PropertyTree();
        final PropertySet data = tree.getRoot();

        data.ifNotNull().setString( ScheduledJobPropertyNames.DESCRIPTION, params.getDescription() );
        data.setBoolean( ScheduledJobPropertyNames.ENABLED, params.isEnabled() );

        addCalendar( params::getCalendar, data );

        if ( params.getDescriptor() != null )
        {
            data.setString( ScheduledJobPropertyNames.DESCRIPTOR, params.getDescriptor().toString() );
        }
        if ( params.getConfig() != null )
        {
            data.setSet( ScheduledJobPropertyNames.CONFIG, params.getConfig().getRoot().copy( data.getTree() ) );
        }
        if ( params.getUser() != null )
        {
            data.setString( ScheduledJobPropertyNames.USER, params.getUser().toString() );
        }

        final Instant now = Instant.now();
        final PrincipalKey contextUser = getCurrentUser().getKey();

        data.setString( ScheduledJobPropertyNames.CREATOR, contextUser.toString() );
        data.setString( ScheduledJobPropertyNames.MODIFIER, contextUser.toString() );
        data.setInstant( ScheduledJobPropertyNames.CREATED_TIME, now );
        data.setInstant( ScheduledJobPropertyNames.MODIFIED_TIME, now );

        return tree;
    }

    public static PropertyTree toUpdateNodeData( final ModifyScheduledJobParams params, final ScheduledJob original )
    {
        final ScheduledJob modifiedJob = editScheduledJob( params.getEditor(), original );

        final PropertyTree tree = new PropertyTree();
        final PropertySet data = tree.getRoot();

        data.ifNotNull().setString( ScheduledJobPropertyNames.DESCRIPTION, modifiedJob.getDescription() );
        data.setBoolean( ScheduledJobPropertyNames.ENABLED, modifiedJob.isEnabled() );

        addCalendar( modifiedJob::getCalendar, data );

        if ( modifiedJob.getDescriptor() != null )
        {
            data.setString( ScheduledJobPropertyNames.DESCRIPTOR, modifiedJob.getDescriptor().toString() );
        }
        if ( modifiedJob.getConfig() != null )
        {
            data.setSet( ScheduledJobPropertyNames.CONFIG, modifiedJob.getConfig().getRoot().copy( data.getTree() ) );
        }
        if ( modifiedJob.getUser() != null )
        {
            data.setString( ScheduledJobPropertyNames.USER, modifiedJob.getUser().toString() );
        }
        if ( modifiedJob.getCreator() != null )
        {
            data.setString( ScheduledJobPropertyNames.CREATOR, modifiedJob.getCreator().toString() );
        }
        if ( modifiedJob.getCreatedTime() != null )
        {
            data.setInstant( ScheduledJobPropertyNames.CREATED_TIME, modifiedJob.getCreatedTime() );
        }

        data.setString( ScheduledJobPropertyNames.MODIFIER, getCurrentUser().getKey().toString() );
        data.setInstant( ScheduledJobPropertyNames.MODIFIED_TIME, Instant.now() );

        return tree;
    }

    public static ScheduledJob fromNode( final Node node )
    {
        final PropertySet data = node.data().getRoot();

        return ScheduledJob.create().
            name( ScheduledJobName.from( node.name().toString() ) ).
            description( data.getString( ScheduledJobPropertyNames.DESCRIPTION ) ).
            enabled( data.getBoolean( ScheduledJobPropertyNames.ENABLED ) ).
            calendar( Optional.ofNullable( data.getSet( ScheduledJobPropertyNames.CALENDAR ) ).
                map( SchedulerSerializer::createCalendar ).
                orElse( null ) ).
            descriptor( Optional.ofNullable( data.getString( ScheduledJobPropertyNames.DESCRIPTOR ) ).
                map( DescriptorKey::from ).
                orElse( null ) ).
            config( Optional.ofNullable( data.getSet( ScheduledJobPropertyNames.CONFIG ) ).
                map( PropertySet::toTree ).
                orElse( null ) ).
            user( Optional.ofNullable( data.getString( ScheduledJobPropertyNames.USER ) ).
                map( PrincipalKey::from ).
                orElse( null ) ).
            lastRun( Optional.ofNullable( data.getInstant( ScheduledJobPropertyNames.LAST_RUN ) ).
                orElse( null ) ).
            lastTaskId( Optional.ofNullable( data.getString( ScheduledJobPropertyNames.LAST_TASK_ID ) ).
                map( TaskId::from ).
                orElse( null ) ).
            creator( Optional.ofNullable( data.getString( ScheduledJobPropertyNames.CREATOR ) ).
                map( PrincipalKey::from ).
                orElse( null ) ).
            modifier( Optional.ofNullable( data.getString( ScheduledJobPropertyNames.MODIFIER ) ).
                map( PrincipalKey::from ).
                orElse( null ) ).
            createdTime( Optional.ofNullable( data.getString( ScheduledJobPropertyNames.CREATED_TIME ) ).
                map( Instant::parse ).
                orElse( null ) ).
            modifiedTime( Optional.ofNullable( data.getString( ScheduledJobPropertyNames.MODIFIED_TIME ) ).
                map( Instant::parse ).
                orElse( null ) ).
            build();
    }

    private static ScheduledJob editScheduledJob( final ScheduledJobEditor editor, final ScheduledJob original )
    {
        final EditableScheduledJob editableJob = new EditableScheduledJob( original );
        if ( editor != null )
        {
            editor.edit( editableJob );
        }
        return editableJob.build();
    }

    private static void addCalendar( final Supplier<ScheduleCalendar> calendarSupplier, final PropertySet data )
    {
        final PropertySet calendarSet = data.getTree().newSet();

        final ScheduleCalendar calendar = calendarSupplier.get();
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
        data.setSet( ScheduledJobPropertyNames.CALENDAR, calendarSet );
    }

    private static ScheduleCalendar createCalendar( final PropertySet data )
    {
        final String value = data.getString( ScheduledJobPropertyNames.CALENDAR_VALUE );
        final String timeZone = data.getString( ScheduledJobPropertyNames.CALENDAR_TIMEZONE );
        final String type = data.getString( ScheduledJobPropertyNames.CALENDAR_TYPE );

        final ScheduleCalendarType calendarType = ScheduleCalendarType.valueOf( type );

        switch ( calendarType )
        {
            case CRON:
                return CronCalendarImpl.create().
                    value( value ).
                    timeZone( TimeZone.getTimeZone( timeZone ) ).
                    build();
            case ONE_TIME:
                return OneTimeCalendarImpl.create().value( Instant.parse( value ) ).build();
            default:
                throw new IllegalArgumentException( String.format( "can't parse [%s] calendar type.", type ) );
        }
    }

    private static User getCurrentUser()
    {
        final Context context = ContextAccessor.current();
        return context.getAuthInfo().getUser() != null ? context.getAuthInfo().getUser() : User.ANONYMOUS;
    }
}
