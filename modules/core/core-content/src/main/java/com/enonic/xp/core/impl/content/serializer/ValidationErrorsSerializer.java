package com.enonic.xp.core.impl.content.serializer;

import java.io.UncheckedIOException;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.enonic.xp.content.AttachmentValidationError;
import com.enonic.xp.content.DataValidationError;
import com.enonic.xp.content.ValidationError;
import com.enonic.xp.content.ValidationErrorCode;
import com.enonic.xp.content.ValidationErrors;
import com.enonic.xp.core.internal.json.ObjectMapperHelper;
import com.enonic.xp.data.PropertyPath;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.util.BinaryReference;

import static com.enonic.xp.content.ContentPropertyNames.VALIDATION_ERRORS;

public class ValidationErrorsSerializer
    extends AbstractDataSetSerializer<ValidationErrors>
{
    private static final ObjectMapper OBJECT_MAPPER = ObjectMapperHelper.create();

    @Override
    public void toData( final ValidationErrors validationErrors, final PropertySet contentAsData )
    {
        if ( validationErrors != null && validationErrors.hasErrors() )
        {
            contentAsData.addSets( VALIDATION_ERRORS, validationErrors.stream().map( validationError -> {
                final PropertySet propertySet = contentAsData.getTree().newSet();
                propertySet.addString( "errorCode", validationError.getErrorCode().toString() );
                propertySet.addString( "message", validationError.getMessage() );
                propertySet.addString( "i18n", validationError.getI18n() );
                if ( !validationError.getArgs().isEmpty() )
                {
                    try
                    {
                        propertySet.addString( "args", OBJECT_MAPPER.writeValueAsString( validationError.getArgs() ) );
                    }
                    catch ( JsonProcessingException e )
                    {
                        throw new UncheckedIOException( e );
                    }
                }

                if ( validationError instanceof DataValidationError )
                {
                    propertySet.addString( "propertyPath", ( (DataValidationError) validationError ).getPropertyPath().toString() );
                }
                else if ( validationError instanceof AttachmentValidationError )
                {
                    propertySet.addString( "attachment", ( (AttachmentValidationError) validationError ).getAttachment().toString() );
                }
                return propertySet;
            } ).toArray( PropertySet[]::new ) );
        }
        else
        {
            contentAsData.removeProperty( VALIDATION_ERRORS );
        }
    }

    @Override
    public ValidationErrors fromData( final PropertySet contentAsData )
    {
        return contentAsData.hasProperty( VALIDATION_ERRORS ) ? ValidationErrors.create()
            .addAll( StreamSupport.stream( contentAsData.getSets( VALIDATION_ERRORS ).spliterator(), false )
                         .map( this::mapValidationError )
                         .collect( Collectors.toList() ) )
            .build() : ValidationErrors.create().build();
    }

    private ValidationError mapValidationError( final PropertySet ve )
    {
        final Object[] args = Optional.ofNullable( ve.getString( "args" ) ).map( argsJson -> {
            try
            {
                return OBJECT_MAPPER.readValue( argsJson, Object[].class );
            }
            catch ( JsonProcessingException e )
            {
                throw new UncheckedIOException( e );
            }
        } ).orElse( null );

        final ValidationErrorCode errorCode = ValidationErrorCode.parse( ve.getString( "errorCode" ) );

        if ( ve.hasProperty( "propertyPath" ) )
        {
            return ValidationError.dataError( errorCode, PropertyPath.from( ve.getString( "propertyPath" ) ) )
                .message( ve.getString( "message" ), true )
                .i18n( ve.getString( "i18n" ) )
                .args( args )
                .build();
        }
        else if ( ve.hasProperty( "attachment" ) )
        {
            return ValidationError.attachmentError( errorCode, BinaryReference.from( ve.getString( "attachment" ) ) )
                .message( ve.getString( "message" ), true )
                .i18n( ve.getString( "i18n" ) )
                .args( args )
                .build();
        }
        else
        {
            return ValidationError.generalError( errorCode )
                .message( ve.getString( "message" ), true )
                .i18n( ve.getString( "i18n" ) )
                .args( args )
                .build();
        }
    }
}
