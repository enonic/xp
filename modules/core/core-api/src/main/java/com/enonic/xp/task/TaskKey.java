package com.enonic.xp.task;

import java.util.Objects;

import org.apache.commons.lang.StringUtils;

import com.google.common.annotations.Beta;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.util.CharacterChecker;

@Beta
public final class TaskKey
{
    private static final String SEPARATOR = ":";

    private final ApplicationKey applicationKey;

    private final String name;

    private final String refString;

    private TaskKey( final ApplicationKey applicationKey, final String name )
    {
        this.applicationKey = applicationKey;
        this.name = CharacterChecker.check( name, "Not a valid Task name [" + name + "]" );
        this.refString = applicationKey.toString() + SEPARATOR + name;
    }

    public ApplicationKey getApplicationKey()
    {
        return applicationKey;
    }

    public String getName()
    {
        return name;
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }
        final TaskKey taskKey = (TaskKey) o;
        return Objects.equals( applicationKey, taskKey.applicationKey ) && Objects.equals( name, taskKey.name );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( applicationKey, name );
    }

    @Override
    public String toString()
    {
        return refString;
    }

    public static TaskKey from( final String key )
    {
        final String applicationKey = StringUtils.substringBefore( key, SEPARATOR );
        final String taskName = StringUtils.substringAfter( key, SEPARATOR );
        return new TaskKey( ApplicationKey.from( applicationKey ), taskName );
    }

    public static TaskKey from( final ApplicationKey applicationKey, final String taskName )
    {
        return new TaskKey( applicationKey, taskName );
    }
}
