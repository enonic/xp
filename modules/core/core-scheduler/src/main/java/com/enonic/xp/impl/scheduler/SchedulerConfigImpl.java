package com.enonic.xp.impl.scheduler;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.enonic.xp.config.ConfigBuilder;
import com.enonic.xp.config.ConfigInterpolator;
import com.enonic.xp.config.Configuration;
import com.enonic.xp.form.PropertyTreeMarshallerService;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.scheduler.CalendarService;
import com.enonic.xp.scheduler.CreateScheduledJobParams;
import com.enonic.xp.scheduler.ScheduleCalendar;
import com.enonic.xp.scheduler.ScheduledJobName;
import com.enonic.xp.security.PrincipalKey;

@Component(configurationPid = "com.enonic.xp.scheduler")
public class SchedulerConfigImpl
    implements SchedulerConfig
{
    private static final String JOB_PROPERTY_PREFIX = "init-job.";

    private static final Pattern JOB_NAME_PATTERN = Pattern.compile( "^(?<name>[\\w\\-]+)\\.[\\w]+$" );

    private static final Pattern JOB_PROPERTY_PATTERN = Pattern.compile( "^(?<property>[a-zA-Z]+)$" );

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final PropertyTreeMarshallerService treeMarshallerService;

    private final CalendarService calendarService;

    private Configuration config;

    @Activate
    public SchedulerConfigImpl( final Map<String, String> map, @Reference final PropertyTreeMarshallerService treeMarshallerService,
                                @Reference final CalendarService calendarService )
    {
        this.treeMarshallerService = treeMarshallerService;
        this.calendarService = calendarService;

        this.config = ConfigBuilder.create().
            load( getClass(), "default.properties" ).
            addAll( map ).
            build();

        this.config = new ConfigInterpolator().interpolate( this.config );
    }

    @Override
    public Set<CreateScheduledJobParams> jobs()
    {
        final Configuration jobConfig = this.config.subConfig( JOB_PROPERTY_PREFIX );

        final Set<ScheduledJobName> jobNames = parseNames( jobConfig );

        return jobNames.stream().
            map( name -> parseProperties( name, jobConfig.subConfig( name.getValue() + "." ) ) ).collect( Collectors.toSet() );

    }

    @Override
    public boolean auditlogEnabled()
    {
        final Boolean enabled = this.config.get( "auditlog.enabled", Boolean.class );
        return enabled == null || enabled;
    }

    private Set<ScheduledJobName> parseNames( final Configuration jobConfig )
    {
        return jobConfig.asMap().keySet().
            stream().
            map( JOB_NAME_PATTERN::matcher ).
            filter( Matcher::find ).
            map( matcher -> matcher.group( "name" ) ).
            map( ScheduledJobName::from ).
            collect( Collectors.toSet() );
    }

    private CreateScheduledJobParams parseProperties( final ScheduledJobName name, final Configuration properties )
    {
        final CreateScheduledJobParams.Builder job = CreateScheduledJobParams.create().name( name );
        TimeZone timeZone = null;
        String cronValue = null;

        for ( final Map.Entry<String, String> entry : properties.asMap().entrySet() )
        {
            final String value = entry.getValue();

            final Matcher matcher = JOB_PROPERTY_PATTERN.matcher( entry.getKey() );
            if ( matcher.matches() )
            {
                final String propertyName = matcher.group( "property" );

                switch ( propertyName )
                {
                    case ScheduledJobPropertyNames.DESCRIPTION:
                        job.description( value );
                        break;
                    case ScheduledJobPropertyNames.DESCRIPTOR:
                        job.descriptor( DescriptorKey.from( value ) );
                        break;
                    case ScheduledJobPropertyNames.ENABLED:
                        job.enabled( Boolean.parseBoolean( value ) );
                        break;
                    case ScheduledJobPropertyNames.USER:
                        job.user( PrincipalKey.from( "user:" + value ) );
                        break;
                    case ScheduledJobPropertyNames.CONFIG:
                        try
                        {
                            job.config( treeMarshallerService.marshal( MAPPER.readValue( value, HashMap.class ) ) );
                        }
                        catch ( JsonProcessingException e )
                        {
                            throw new RuntimeException( e );
                        }
                        break;
                    case ScheduledJobPropertyNames.CALENDAR_TIMEZONE:
                        timeZone = TimeZone.getTimeZone( value );
                        break;
                    case "cron":
                        cronValue = value;
                        break;
                    default:
                        throw new IllegalArgumentException( String.format( "[%s] is invalid job property.", propertyName ) );
                }
            }
        }
        final ScheduleCalendar calendar = calendarService.cron( cronValue, timeZone != null ? timeZone : TimeZone.getDefault() );

        return job.
            calendar( calendar ).
            build();
    }


}
