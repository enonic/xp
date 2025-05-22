package com.enonic.xp.lib.scheduler;

import java.util.Map;

import com.google.common.base.Preconditions;

import com.enonic.xp.lib.scheduler.mapper.ScheduledJobMapper;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.scheduler.EditableScheduledJob;
import com.enonic.xp.scheduler.ModifyScheduledJobParams;
import com.enonic.xp.scheduler.ScheduledJob;
import com.enonic.xp.scheduler.ScheduledJobEditor;
import com.enonic.xp.scheduler.ScheduledJobName;
import com.enonic.xp.script.ScriptValue;
import com.enonic.xp.security.PrincipalKey;

public final class ModifyScheduledJobHandler
    extends BaseSchedulerHandler
{
    private ScheduledJobName name;

    private ScriptValue editor;

    @Override
    protected ScheduledJobMapper doExecute()
    {
        final ScheduledJob existingJob = schedulerService.get().get( name );
        if ( existingJob == null )
        {
            throw new IllegalArgumentException( String.format( "[%s] job not found.", name.getValue() ) );
        }

        final ModifyScheduledJobParams params = ModifyScheduledJobParams.create().name( name ).editor( newJobEditor() ).build();

        final ScheduledJob modifiedJob = this.schedulerService.get().modify( params );

        return ScheduledJobMapper.from( modifiedJob );
    }

    private ScheduledJobEditor newJobEditor()
    {
        return edit -> {
            final ScriptValue value = this.editor.call( ScheduledJobMapper.from( edit.build() ) );
            if ( value != null )
            {
                updateJob( edit, value );
            }
        };
    }

    private void updateJob( final EditableScheduledJob target, final ScriptValue params )
    {
        updateDescriptor( target, params );
        updateConfig( target, params );
        updateCalendar( target, params );
        updateIsEnabled( target, params );
        updateDescription( target, params );
        updateUser( target, params );
    }

    private void updateDescriptor( final EditableScheduledJob target, final ScriptValue params )
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
    }

    private void updateConfig( final EditableScheduledJob target, final ScriptValue params )
    {
        if ( params.getKeys().contains( "config" ) )
        {
            final ScriptValue value = params.getMember( "config" );
            if ( value == null )
            {
                throw new IllegalArgumentException( "config cannot be null" );
            }
            target.config = propertyTreeMarshallerService.get().marshal( params.getMember( "config" ).getMap() );
        }
    }

    private void updateCalendar( final EditableScheduledJob target, final ScriptValue params )
    {
        if ( params.getKeys().contains( "schedule" ) )
        {
            final ScriptValue value = params.getMember( "schedule" );
            if ( value == null )
            {
                throw new IllegalArgumentException( "schedule cannot be null" );
            }
            target.calendar = buildCalendar( (Map) params.getMember( "schedule" ).getMap() );
        }
    }

    private void updateIsEnabled( final EditableScheduledJob target, final ScriptValue params )
    {
        if ( params.getKeys().contains( "enabled" ) )
        {
            final ScriptValue value = params.getMember( "enabled" );
            if ( value == null )
            {
                throw new IllegalArgumentException( "enabled cannot be null" );
            }
            target.enabled = value.getValue( boolean.class );
        }
    }

    private void updateDescription( final EditableScheduledJob target, final ScriptValue params )
    {
        if ( params.getKeys().contains( "description" ) )
        {
            target.description =
                params.getMember( "description" ) != null ? params.getMember( "description" ).getValue( String.class ) : null;
        }
    }

    private void updateUser( final EditableScheduledJob target, final ScriptValue params )
    {
        if ( params.getKeys().contains( "user" ) )
        {
            final ScriptValue value = params.getMember( "user" );
            target.user = value != null ? PrincipalKey.from( value.getValue( String.class ) ) : null;
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
        this.name = ScheduledJobName.from( value );
    }

    public void setEditor( final ScriptValue editor )
    {
        this.editor = editor;
    }
}
