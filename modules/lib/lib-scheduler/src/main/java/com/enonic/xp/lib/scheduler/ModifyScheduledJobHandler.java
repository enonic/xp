package com.enonic.xp.lib.scheduler;

import java.util.Map;

import com.google.common.base.Preconditions;

import com.enonic.xp.lib.scheduler.mapper.ScheduledJobMapper;
import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.scheduler.EditableScheduledJob;
import com.enonic.xp.scheduler.ModifyScheduledJobParams;
import com.enonic.xp.scheduler.ScheduledJob;
import com.enonic.xp.scheduler.ScheduledJobEditor;
import com.enonic.xp.scheduler.SchedulerName;
import com.enonic.xp.script.ScriptValue;
import com.enonic.xp.security.PrincipalKey;

public final class ModifyScheduledJobHandler
    extends BaseSchedulerHandler
{
    private SchedulerName name;

    private ScriptValue editor;

    @Override
    protected ScheduledJobMapper doExecute()
    {
        final ScheduledJob existingJob = schedulerService.get().get( name );
        if ( existingJob == null )
        {
            throw new IllegalArgumentException( String.format( "[%s] job not found.", name.getValue() ) );
        }

        final ModifyScheduledJobParams params =
            ModifyScheduledJobParams.create().name( name ).editor( newJobEditor( existingJob ) ).build();

        final ScheduledJob modifiedJob = this.schedulerService.get().modify( params );

        return ScheduledJobMapper.from( modifiedJob );
    }

    private ScheduledJobEditor newJobEditor( final ScheduledJob existingJob )
    {
        return edit -> {
            final ScriptValue value = this.editor.call( ScheduledJobMapper.from( edit.build() ) );
            if ( value != null )
            {
                updateJob( edit, value, existingJob );
            }
        };
    }

    private void updateJob( final EditableScheduledJob target, final ScriptValue params, final ScheduledJob existingJob )
    {
        if ( params.getKeys().contains( "descriptor" ) )
        {
            final ScriptValue value = params.getMember( "descriptor" );
            if ( value == null )
            {
                throw new IllegalArgumentException( "descriptor cannot be null" );
            }
            target.descriptor = DescriptorKey.from( value.getValue( String.class ) );
        }

        if ( params.getKeys().contains( "payload" ) )
        {
            final ScriptValue value = params.getMember( "payload" );
            if ( value == null )
            {
                throw new IllegalArgumentException( "payload cannot be null" );
            }
            target.payload = propertyTreeMarshallerService.get().marshal( params.getMember( "payload" ).getMap() );
        }

        if ( params.getKeys().contains( "calendar" ) )
        {
            final ScriptValue value = params.getMember( "calendar" );
            if ( value == null )
            {
                throw new IllegalArgumentException( "calendar cannot be null" );
            }
            target.calendar = buildCalendar( (Map) params.getMember( "calendar" ).getMap() );
        }

        if ( params.getKeys().contains( "enabled" ) )
        {
            final ScriptValue value = params.getMember( "enabled" );
            if ( value == null )
            {
                throw new IllegalArgumentException( "enabled cannot be null" );
            }
            target.enabled = value.getValue( boolean.class );
        }

        if ( params.getKeys().contains( "description" ) )
        {
            target.description =
                params.getMember( "description" ) != null ? params.getMember( "description" ).getValue( String.class ) : null;
        }
        if ( params.getKeys().contains( "user" ) )
        {
            final ScriptValue value = params.getMember( "user" );
            target.user = value != null ? PrincipalKey.from( value.getValue( String.class ) ) : null;
        }
        if ( params.getKeys().contains( "author" ) )
        {
            final ScriptValue value = params.getMember( "author" );
            target.author = value != null ? PrincipalKey.from( value.getValue( String.class ) ) : null;
        }

    }

    @Override
    protected void validate()
    {
        Preconditions.checkArgument( name != null && !name.getValue().isBlank(), "name must be set." );
        Preconditions.checkArgument( editor != null, "editor must be set." );
    }

    public void setName( final String value )
    {
        this.name = SchedulerName.from( value );
    }

    public void setEditor( final ScriptValue editor )
    {
        this.editor = editor;
    }
}
