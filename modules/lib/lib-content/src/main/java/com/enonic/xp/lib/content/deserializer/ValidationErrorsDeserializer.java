package com.enonic.xp.lib.content.deserializer;

import java.util.List;
import java.util.Map;

import com.enonic.xp.content.ValidationError;
import com.enonic.xp.content.ValidationErrorCode;
import com.enonic.xp.content.ValidationErrors;

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

            final String errorCodeStr = asString( errorMap.get( "errorCode" ) );
            if ( errorCodeStr == null )
            {
                throw new IllegalArgumentException( "Missing required field 'errorCode'" );
            }

            final ValidationErrorCode errorCode = ValidationErrorCode.parse( errorCodeStr );

            final String message = asString( errorMap.get( "message" ) );
            final String i18n = asString( errorMap.get( "i18n" ) );
            final List<Object> args = (List<Object>) errorMap.get( "args" );

            final ValidationError.Builder errorBuilder =
                ValidationError.generalError( errorCode ).message( message ).i18n( i18n );

            if ( args != null )
            {
                errorBuilder.args( args.toArray() );
            }

            builder.add( errorBuilder.build() );
        }

        return builder.build();
    }

    private String asString( Object obj )
    {
        return obj != null ? obj.toString() : null;
    }
}
