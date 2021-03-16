package com.enonic.xp.lib.scheduler;

import java.util.Map;
import java.util.Optional;

import com.google.common.base.Preconditions;

import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.lib.scheduler.mapper.ScheduledJobMapper;
import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.scheduler.CreateScheduledJobParams;
import com.enonic.xp.scheduler.ScheduleCalendar;
import com.enonic.xp.scheduler.ScheduledJob;
import com.enonic.xp.scheduler.SchedulerName;
import com.enonic.xp.security.PrincipalKey;

public final class CreateScheduledJobHandler
    extends BaseSchedulerHandler
{
    private SchedulerName name;

    private String description;

    private ScheduleCalendar calendar;

    private boolean enabled;

    private DescriptorKey descriptor;

    private PropertyTree payload;

    private PrincipalKey user;

    private PrincipalKey author;

    @Override
    protected ScheduledJobMapper doExecute()
    {
        final CreateScheduledJobParams params = createParams();
        final ScheduledJob scheduledJob = this.schedulerService.get().create( params );

        return ScheduledJobMapper.from( scheduledJob );
    }

    private CreateScheduledJobParams createParams()
    {
        return CreateScheduledJobParams.create().
            name( name ).
            descriptor( descriptor ).
            calendar( calendar ).
            description( description ).
            payload( payload ).
            enabled( enabled ).
            author( author ).
            user( user ).
            build();
    }

    @Override
    protected void validate()
    {
        Preconditions.checkArgument( name != null && !name.getValue().isBlank(), "name must be set." );
        Preconditions.checkArgument( calendar != null, "calendar must be set." );
        Preconditions.checkArgument( descriptor != null, "descriptor must be set." );
    }

    public void setName( final String value )
    {
        this.name = SchedulerName.from( value );
    }

    public void setDescriptor( final String value )
    {
        this.descriptor = DescriptorKey.from( value );
    }

    public void setDescription( final String value )
    {
        this.description = value;
    }

    public void setCalendar( final Map<String, String> value )
    {
        this.calendar = buildCalendar( value );
    }

    public void setPayload( final Map<String, Object> value )
    {
        this.payload = Optional.ofNullable( propertyTreeMarshallerService.get().marshal( value ) ).orElse( null );
    }

    public void setEnabled( final boolean value )
    {
        this.enabled = value;
    }

    public void setAuthor( final String value )
    {
        this.author = Optional.ofNullable( value ).map( PrincipalKey::from ).orElse( null );
    }

    public void setUser( final String value )
    {
        this.user = Optional.ofNullable( value ).map( PrincipalKey::from ).orElse( null );
    }
}
