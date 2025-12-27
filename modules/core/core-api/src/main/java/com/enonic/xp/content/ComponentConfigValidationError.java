package com.enonic.xp.content;

import java.util.List;
import java.util.Objects;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.data.PropertyPath;
import com.enonic.xp.region.ComponentPath;

public final class ComponentConfigValidationError
    extends DataValidationError
{
    private final ApplicationKey applicationKey;

    private final ComponentPath componentPath;

    ComponentConfigValidationError( final PropertyPath propertyPath, final ValidationErrorCode errorCode, final String message,
                                    final String i18n, final ApplicationKey applicationKey, final ComponentPath componentPath,
                                    final List<Object> args )
    {
        super( propertyPath, errorCode, message, i18n, args );
        this.applicationKey = Objects.requireNonNull( applicationKey );
        this.componentPath = Objects.requireNonNull( componentPath );
    }

    public ApplicationKey getApplicationKey()
    {
        return applicationKey;
    }

    public ComponentPath getComponentPath()
    {
        return componentPath;
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
        if ( !super.equals( o ) )
        {
            return false;
        }
        final ComponentConfigValidationError that = (ComponentConfigValidationError) o;
        return applicationKey.equals( that.applicationKey ) && componentPath.equals( that.componentPath );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( super.hashCode(), applicationKey, componentPath );
    }
}
