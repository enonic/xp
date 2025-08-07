package com.enonic.xp.lib.scheduler;

import java.util.Map;
import java.util.Optional;

import com.google.common.base.Preconditions;

import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.lib.scheduler.mapper.ScheduledJobMapper;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.scheduler.CreateScheduledJobParams;
import com.enonic.xp.scheduler.ScheduleCalendar;
import com.enonic.xp.scheduler.ScheduledJob;
import com.enonic.xp.scheduler.ScheduledJobName;
import com.enonic.xp.script.ScriptValue;
import com.enonic.xp.security.PrincipalKey;

public final class CreateScheduledJobHandler
    extends BaseSchedulerHandler
{
    private ScheduledJobName name;

    private String description;

    private ScheduleCalendar schedule;

    private boolean enabled;

    private DescriptorKey descriptor;

    private PropertyTree config;

    private PrincipalKey user;

    @Override
    protected ScheduledJobMapper doExecute()
    {
        final CreateScheduledJobParams params = createParams();
        final ScheduledJob scheduledJob = this.schedulerService.get().create( params );

        return ScheduledJobMapper.from( scheduledJob );
    }

    private CreateScheduledJobParams createParams()
    {
        return CreateScheduledJobParams.create()
            .name( name )
            .descriptor( descriptor )
            .calendar( schedule )
            .description( description )
            .config( config )
            .enabled( enabled )
            .user( user )
            .build();
    }

    @Override
    protected void validate()
    {
        Preconditions.checkArgument( name != null && !name.getValue().isBlank(), "name must be set." );
        Preconditions.checkArgument( schedule != null, "calendar must be set." );
        Preconditions.checkArgument( descriptor != null, "descriptor must be set." );
    }

    public void setName( final String value )
    {
        this.name = ScheduledJobName.from( value );
    }

    public void setDescriptor( final String value )
    {
        this.descriptor = DescriptorKey.from( value );
    }

    public void setDescription( final String value )
    {
        this.description = value;
    }

    public void setSchedule( final Map<String, String> value )
    {
        this.schedule = buildCalendar( value );
    }

    public void setConfig( final ScriptValue value )
    {
        this.config =
            PropertyTree.fromMap( Optional.ofNullable( value ).map( ScriptValue::getMap ).orElse( Map.of() ) );
    }

    public void setEnabled( final boolean value )
    {
        this.enabled = value;
    }

    public void setUser( final String value )
    {
        this.user = Optional.ofNullable( value ).map( PrincipalKey::from ).orElse( null );
    }
}
