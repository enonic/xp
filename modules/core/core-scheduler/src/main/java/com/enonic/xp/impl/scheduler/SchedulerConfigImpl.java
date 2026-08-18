package com.enonic.xp.impl.scheduler;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.enonic.xp.config.ConfigBuilder;
import com.enonic.xp.config.ConfigInterpolator;
import com.enonic.xp.config.Configuration;
import com.enonic.xp.data.PropertyTree;
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

    private static final Logger LOG = LoggerFactory.getLogger( SchedulerConfigImpl.class );

    private final CalendarService calendarService;

    private volatile Configuration config;

    private volatile Set<CreateScheduledJobParams> jobs;

    @Activate
    public SchedulerConfigImpl( final Map<String, String> map, @Reference final CalendarService calendarService )
    {
        this.calendarService = calendarService;
        apply( map );
    }

    /**
     * Reconfiguration is applied in place, so nothing that reads this is restarted by an edit. What
     * reads it must therefore read it when it needs it rather than once at its own start - a job
     * added to the configuration is picked up by the next tick, and audit logging can be turned off
     * while jobs are running.
     */
    @Modified
    public void modify( final Map<String, String> map )
    {
        apply( map );
    }

    private void apply( final Map<String, String> map )
    {
        this.config = buildConfig( map );
        // parsed here rather than on demand: the scheduler asks for the jobs every tick, and a
        // description it cannot make sense of is a problem with the file, not with that tick
        this.jobs = parseJobs();
    }

    private static Configuration buildConfig( final Map<String, String> map )
    {
        return new ConfigInterpolator().interpolate(
            ConfigBuilder.create().load( SchedulerConfigImpl.class, "default.properties" ).addAll( map ).build() );
    }

    @Override
    public Set<CreateScheduledJobParams> jobs()
    {
        return jobs;
    }

    private Set<CreateScheduledJobParams> parseJobs()
    {
        final Configuration jobConfig = this.config.subConfig( JOB_PROPERTY_PREFIX );

        final Set<CreateScheduledJobParams> parsed = new HashSet<>();
        for ( final ScheduledJobName name : parseNames( jobConfig ) )
        {
            try
            {
                parsed.add( parseProperties( name, jobConfig.subConfig( name.getValue() + "." ) ) );
            }
            catch ( Exception e )
            {
                // an entry that cannot be understood is left out with an explanation, rather than
                // taken as a reason to ignore every other job described in the same file
                LOG.error( "Invalid configuration of job [{}], it is ignored", name, e );
            }
        }
        return Set.copyOf( parsed );
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
                            job.config( PropertyTree.fromMap( MAPPER.readValue( value, HashMap.class ) ) );
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
