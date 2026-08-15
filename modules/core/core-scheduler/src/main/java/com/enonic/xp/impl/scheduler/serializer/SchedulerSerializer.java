package com.enonic.xp.impl.scheduler.serializer;

import java.time.Instant;
import java.util.Optional;
import java.util.TimeZone;
import java.util.function.Function;
import java.util.function.Supplier;

import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.core.internal.Millis;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.impl.scheduler.ScheduledJobPropertyNames;
import com.enonic.xp.impl.scheduler.distributed.CronCalendarImpl;
import com.enonic.xp.impl.scheduler.distributed.FixedDelayCalendarImpl;
import com.enonic.xp.impl.scheduler.distributed.OneTimeCalendarImpl;
import com.enonic.xp.node.Attributes;
import com.enonic.xp.node.Node;
import com.enonic.xp.scheduler.CreateScheduledJobParams;
import com.enonic.xp.scheduler.CronCalendar;
import com.enonic.xp.scheduler.EditableScheduledJob;
import com.enonic.xp.scheduler.FixedDelayCalendar;
import com.enonic.xp.scheduler.ModifyScheduledJobParams;
import com.enonic.xp.scheduler.OneTimeCalendar;
import com.enonic.xp.scheduler.ScheduleCalendar;
import com.enonic.xp.scheduler.ScheduleCalendarType;
import com.enonic.xp.scheduler.ScheduledJob;
import com.enonic.xp.scheduler.ScheduledJobEditor;
import com.enonic.xp.scheduler.ScheduledJobName;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.task.TaskId;
import com.enonic.xp.util.GenericValue;

public class SchedulerSerializer
{

    private SchedulerSerializer()
    {
    }

    public static PropertyTree toCreateNodeData( final CreateScheduledJobParams params )
    {
        final PropertyTree tree = new PropertyTree();
        final PropertySet data = tree.getRoot();

        data.resetString( ScheduledJobPropertyNames.DESCRIPTION, params.getDescription() );
        data.setBoolean( ScheduledJobPropertyNames.ENABLED, params.isEnabled() );
        data.setBoolean( ScheduledJobPropertyNames.DELETE_AFTER_RUN, params.isDeleteAfterRun() );

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

        final Instant now = Millis.now();
        final PrincipalKey contextUser = getCurrentUserKey();

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

        data.setString( ScheduledJobPropertyNames.DESCRIPTION, modifiedJob.getDescription() );
        data.setBoolean( ScheduledJobPropertyNames.ENABLED, modifiedJob.isEnabled() );
        data.setBoolean( ScheduledJobPropertyNames.DELETE_AFTER_RUN, modifiedJob.isDeleteAfterRun() );

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

        data.setString( ScheduledJobPropertyNames.MODIFIER, getCurrentUserKey().toString() );
        data.setInstant( ScheduledJobPropertyNames.MODIFIED_TIME, Millis.now() );

        return tree;
    }

    public static ScheduledJob fromNode( final Node node )
    {
        return fromNode( node, null );
    }

    public static Attributes toLastRunAttributes( final Instant lastRun, final TaskId lastTaskId )
    {
        final GenericValue.ObjectBuilder builder = GenericValue.newObject().
            put( ScheduledJobPropertyNames.LAST_RUN_TIME_PROPERTY, lastRun.toString() );
        if ( lastTaskId != null )
        {
            builder.put( ScheduledJobPropertyNames.LAST_RUN_TASK_ID_PROPERTY, lastTaskId.toString() );
        }
        return Attributes.create().
            attribute( ScheduledJobPropertyNames.LAST_RUN_ATTRIBUTE, builder.build() ).
            build();
    }

    public static ScheduledJob fromNode( final Node node, final Attributes attributes )
    {
        final PropertySet data = node.data().getRoot();

        final Optional<Instant> legacyAttributeLastRun = Optional.ofNullable( attributes )
            .flatMap( value -> parseAttribute( value.get( ScheduledJobPropertyNames.LAST_RUN ), Instant::parse ) );
        final Optional<TaskId> legacyAttributeLastTaskId = Optional.ofNullable( attributes )
            .flatMap( value -> parseAttribute( value.get( ScheduledJobPropertyNames.LAST_TASK_ID ), TaskId::from ) );
        final Instant legacyLastRun = legacyAttributeLastRun.orElse( data.getInstant( ScheduledJobPropertyNames.LAST_RUN ) );
        final TaskId legacyLastTaskId = legacyAttributeLastRun.isPresent()
            ? legacyAttributeLastTaskId.orElse( null )
            : Optional.ofNullable( data.getString( ScheduledJobPropertyNames.LAST_TASK_ID ) ).map( TaskId::from ).orElse( null );

        final Optional<GenericValue> lastRunAttribute = Optional.ofNullable( attributes )
            .map( value -> value.get( ScheduledJobPropertyNames.LAST_RUN_ATTRIBUTE ) );
        final Optional<Instant> attributeLastRun = lastRunAttribute
            .flatMap( value -> value.optional( ScheduledJobPropertyNames.LAST_RUN_TIME_PROPERTY ) )
            .flatMap( value -> parseAttribute( value, Instant::parse ) );
        final Optional<TaskId> attributeLastTaskId = lastRunAttribute
            .flatMap( value -> value.optional( ScheduledJobPropertyNames.LAST_RUN_TASK_ID_PROPERTY ) )
            .flatMap( value -> parseAttribute( value, TaskId::from ) );
        final Instant lastRun = attributeLastRun.orElse( legacyLastRun );
        final TaskId lastTaskId = attributeLastRun.isPresent()
            ? attributeLastTaskId.orElse( null )
            : legacyLastTaskId;

        return ScheduledJob.create()
            .name( ScheduledJobName.from( node.name().toString() ) )
            .description( data.getString( ScheduledJobPropertyNames.DESCRIPTION ) )
            .enabled( data.getBoolean( ScheduledJobPropertyNames.ENABLED ) )
            .deleteAfterRun( Boolean.TRUE.equals( data.getBoolean( ScheduledJobPropertyNames.DELETE_AFTER_RUN ) ) )
            .calendar( Optional.ofNullable( data.getSet( ScheduledJobPropertyNames.CALENDAR ) )
                           .map( SchedulerSerializer::createCalendar )
                           .orElse( null ) )
            .descriptor(
                Optional.ofNullable( data.getString( ScheduledJobPropertyNames.DESCRIPTOR ) ).map( DescriptorKey::from ).orElse( null ) )
            .config( Optional.ofNullable( data.getSet( ScheduledJobPropertyNames.CONFIG ) ).map( PropertySet::toTree ).orElse( null ) )
            .user( Optional.ofNullable( data.getString( ScheduledJobPropertyNames.USER ) ).map( PrincipalKey::from ).orElse( null ) )
            .lastRun( lastRun )
            .lastTaskId( lastTaskId )
            .creator( Optional.ofNullable( data.getString( ScheduledJobPropertyNames.CREATOR ) ).map( PrincipalKey::from ).orElse( null ) )
            .modifier(
                Optional.ofNullable( data.getString( ScheduledJobPropertyNames.MODIFIER ) ).map( PrincipalKey::from ).orElse( null ) )
            .createdTime(
                Optional.ofNullable( data.getString( ScheduledJobPropertyNames.CREATED_TIME ) ).map( Instant::parse ).orElse( null ) )
            .modifiedTime(
                Optional.ofNullable( data.getString( ScheduledJobPropertyNames.MODIFIED_TIME ) ).map( Instant::parse ).orElse( null ) )
            .build();
    }

    private static <T> Optional<T> parseAttribute( final GenericValue value, final Function<String, T> parser )
    {
        if ( value == null )
        {
            return Optional.empty();
        }
        try
        {
            return Optional.of( parser.apply( value.asString() ) );
        }
        catch ( RuntimeException e )
        {
            return Optional.empty();
        }
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

            case FIXED_DELAY:
                final FixedDelayCalendar fixedDelayCalendar = ( (FixedDelayCalendar) calendar );
                calendarSet.setString( ScheduledJobPropertyNames.CALENDAR_VALUE, fixedDelayCalendar.getDuration().toString() );
                calendarSet.setString( ScheduledJobPropertyNames.CALENDAR_TYPE, ScheduleCalendarType.FIXED_DELAY.name() );
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
                return CronCalendarImpl.create().value( value ).timeZone( TimeZone.getTimeZone( timeZone ) ).build();
            case ONE_TIME:
                return OneTimeCalendarImpl.create().value( Instant.parse( value ) ).build();
            case FIXED_DELAY:
                return FixedDelayCalendarImpl.create().duration( java.time.Duration.parse( value ) ).build();
            default:
                throw new IllegalArgumentException( String.format( "can't parse [%s] calendar type.", type ) );
        }
    }

    private static PrincipalKey getCurrentUserKey()
    {
        final Context context = ContextAccessor.current();
        return context.getAuthInfo().getUser() != null ? context.getAuthInfo().getUser().getKey() : PrincipalKey.ofAnonymous();
    }
}
