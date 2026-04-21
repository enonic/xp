package com.enonic.xp.lib.content.deserializer;

import java.util.List;
import java.util.Map;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.content.ValidationError;
import com.enonic.xp.content.ValidationErrorCode;
import com.enonic.xp.content.ValidationErrors;
import com.enonic.xp.data.PropertyPath;
import com.enonic.xp.util.BinaryReference;

public final class ValidationErrorsDeserializer
{
    public ValidationErrors deserialize( final List<Object> list )
    {
        if ( list == null || list.isEmpty() )
        {
            return null;
        }

        final ValidationErrors.Builder builder = ValidationErrors.create();

        for ( Object item : list )
        {
            if ( !( item instanceof Map ) )
            {
                throw new IllegalArgumentException( "Each validation error must be a map" );
            }

            Map<String, Object> errorMap = (Map<String, Object>) item;

            final ValidationErrorCode errorCode = parseErrorCode( errorMap.get( "errorCode" ) );

            final String message = asString( errorMap.get( "message" ) );
            final String i18n = asString( errorMap.get( "i18n" ) );
            final List<Object> args = (List<Object>) errorMap.get( "args" );

            final String attachmentStr = asString( errorMap.get( "attachment" ) );
            final String propertyPathStr = asString( errorMap.get( "propertyPath" ) );

            ValidationError.Builder errorBuilder;

            if ( attachmentStr != null )
            {
                errorBuilder = ValidationError.attachmentError( errorCode, BinaryReference.from( attachmentStr ) );
            }
            else if ( propertyPathStr != null )
            {
                errorBuilder = ValidationError.dataError( errorCode, PropertyPath.from( propertyPathStr ) );
            }
            else
            {
                errorBuilder = ValidationError.generalError( errorCode );
            }

            if ( message != null )
            {
                errorBuilder.message( message );
            }

            if ( i18n != null )
            {
                errorBuilder.i18n( i18n );
            }

            if ( args != null && !args.isEmpty() )
            {
                errorBuilder.args( args.toArray() );
            }

            builder.add( errorBuilder.build() );
        }

        return builder.build();
    }

    private ValidationErrorCode parseErrorCode( final Object errorCodeObj )
    {
        if ( !( errorCodeObj instanceof Map ) )
        {
            throw new IllegalArgumentException( "Missing required field 'errorCode'" );
        }
        final Map<String, Object> errorCodeMap = (Map<String, Object>) errorCodeObj;
        final String applicationKey = asString( errorCodeMap.get( "applicationKey" ) );
        final String code = asString( errorCodeMap.get( "code" ) );
        if ( applicationKey == null || code == null )
        {
            throw new IllegalArgumentException( "errorCode must have 'applicationKey' and 'code' fields" );
        }
        return ValidationErrorCode.from( ApplicationKey.from( applicationKey ), code );
    }

    private String asString( Object obj )
    {
        return obj != null ? obj.toString() : null;
    }
}
