package com.enonic.xp.content;

import java.util.List;
import java.util.Objects;

import com.enonic.xp.data.PropertyPath;
import com.enonic.xp.schema.mixin.MixinName;

public final class MixinConfigValidationError
    extends DataValidationError
{
    private final MixinName mixinName;

    MixinConfigValidationError( final PropertyPath propertyPath, final ValidationErrorCode errorCode, final String message,
                                final String i18n, final MixinName mixinName, final List<Object> args )
    {
        super( propertyPath, errorCode, message, i18n, args );
        this.mixinName = Objects.requireNonNull( mixinName );
    }

    public MixinName getMixinName()
    {
        return mixinName;
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
        final MixinConfigValidationError that = (MixinConfigValidationError) o;
        return mixinName.equals( that.mixinName );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( super.hashCode(), mixinName );
    }
}
