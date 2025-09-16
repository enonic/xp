package com.enonic.xp.content;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.enonic.xp.data.PropertyPath;
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
        this.args = Collections.unmodifiableList( args );
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

        private String i18n;

        private Object[] args;

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
            this.args = args;
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

            final String formattedMessage =
                skipFormat || args == null || args.length == 0 || message == null ? message : MessageFormat.format( message, args );

            final List<Object> argsList = args == null ? List.of() : Arrays.stream( args ).map( arg -> {
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
