package com.enonic.xp.content;

import java.text.MessageFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.data.PropertyPath;
import com.enonic.xp.region.ComponentPath;
import com.enonic.xp.schema.mixin.MixinName;
import com.enonic.xp.util.BinaryReference;

public sealed class ValidationError
    permits DataValidationError, AttachmentValidationError
{
    private final ValidationErrorCode errorCode;

    private final String message;

    private final String i18n;

    private final List<Object> args;

    ValidationError( final ValidationErrorCode errorCode, final String message, final String i18n, final List<Object> args )
    {
        this.errorCode = errorCode;
        this.message = message;
        this.i18n = i18n;
        this.args = args;
    }

    public String getMessage()
    {
        return message;
    }

    public ValidationErrorCode getErrorCode()
    {
        return errorCode;
    }

    public String getI18n()
    {
        return i18n;
    }

    public List<Object> getArgs()
    {
        return args;
    }

    public static Builder generalError( final ValidationErrorCode errorCode )
    {
        final Builder builder = new Builder();
        builder.errorCode = errorCode;
        return builder;
    }

    public static Builder attachmentError( final ValidationErrorCode errorCode, final BinaryReference attachment )
    {
        final Builder builder = new Builder();
        builder.errorCode = errorCode;
        builder.attachment = attachment;
        return builder;
    }

    public static Builder dataError( final ValidationErrorCode errorCode, final PropertyPath propertyPath )
    {
        final Builder builder = new Builder();
        builder.errorCode = errorCode;
        builder.propertyPath = propertyPath;
        return builder;
    }

    public static Builder siteConfigError( final ValidationErrorCode errorCode, final PropertyPath propertyPath,
                                           final ApplicationKey applicationKey )
    {
        Builder builder = dataError( errorCode, propertyPath );
        builder.applicationKey = applicationKey;
        return builder;
    }

    public static Builder mixinConfigError( final ValidationErrorCode errorCode, final PropertyPath propertyPath,
                                            final MixinName mixinName )
    {
        Builder builder = dataError( errorCode, propertyPath );
        builder.mixinName = mixinName;
        return builder;
    }

    public static Builder componentConfigError( final ValidationErrorCode errorCode, final PropertyPath propertyPath,
                                                final ApplicationKey applicationKey, final ComponentPath componentPath )
    {
        Builder builder = siteConfigError( errorCode, propertyPath, applicationKey );
        builder.componentPath = componentPath;
        return builder;
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
        final ValidationError that = (ValidationError) o;
        return Objects.equals( errorCode, that.errorCode ) && Objects.equals( message, that.message ) &&
            Objects.equals( i18n, that.i18n ) && args.equals( that.args );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( errorCode, message, i18n, args );
    }

    public static class Builder
    {
        private ValidationErrorCode errorCode;

        private BinaryReference attachment;

        private PropertyPath propertyPath;

        private final ImmutableList.Builder<Object> argsBuilder = ImmutableList.builder();

        private ApplicationKey applicationKey;

        private ComponentPath componentPath;

        private String i18n;

        private MixinName mixinName;

        private String message;

        private boolean skipFormat;

        private Builder()
        {
        }

        public Builder i18n( final String i18n )
        {
            this.i18n = i18n;
            return this;
        }

        public Builder args( final Object... args )
        {
            if ( args != null )
            {
                this.argsBuilder.addAll( ImmutableList.copyOf( args ) );
            }
            return this;
        }

        public Builder message( final String message )
        {
            this.message = message;
            return this;
        }

        public Builder message( final String message, boolean skipFormat )
        {
            this.message = message;
            this.skipFormat = skipFormat;
            return this;
        }

        public ValidationError build()
        {
            Objects.requireNonNull( errorCode );

            final List<Object> args = this.argsBuilder.build();

            final String formattedMessage =
                skipFormat || args.isEmpty() || message == null ? message : MessageFormat.format( message, args.toArray() );

            final List<Object> argsList = args.stream().map( arg -> {
                if ( arg instanceof Number )
                {
                    return arg;
                }
                else if ( arg instanceof Date )
                {
                    return ( (Date) arg ).getTime();
                }
                else
                {
                    return arg == null ? null : arg.toString();
                }
            } ).collect( Collectors.toList() );

            if ( attachment != null )
            {
                return new AttachmentValidationError( attachment, errorCode, formattedMessage, i18n, argsList );
            }
            else if ( mixinName != null )
            {
                return new MixinConfigValidationError( propertyPath, errorCode, formattedMessage, i18n, mixinName, argsList );
            }
            else if ( componentPath != null )
            {
                return new ComponentConfigValidationError( propertyPath, errorCode, formattedMessage, i18n, applicationKey, componentPath,
                                                           argsList );
            }
            else if ( applicationKey != null )
            {
                return new SiteConfigValidationError( propertyPath, errorCode, formattedMessage, i18n, applicationKey, argsList );
            }
            else if ( propertyPath != null )
            {
                return new DataValidationError( propertyPath, errorCode, formattedMessage, i18n, argsList );
            }
            else
            {
                return new ValidationError( errorCode, formattedMessage, i18n, argsList );
            }
        }
    }
}
