package com.enonic.xp.lib.content.mapper;

import java.util.List;

import com.enonic.xp.content.AttachmentValidationError;
import com.enonic.xp.content.DataValidationError;
import com.enonic.xp.content.ValidationErrors;
import com.enonic.xp.script.serializer.MapGenerator;
import com.enonic.xp.script.serializer.MapSerializable;

public final class ValidationErrorsMapper
    implements MapSerializable
{
    private final ValidationErrors value;

    public ValidationErrorsMapper( final ValidationErrors value )
    {
        this.value = value;
    }

    @Override
    public void serialize( final MapGenerator gen )
    {
        gen.array( "validationErrors" );

        if ( value != null )
        {
            value.stream().forEach( error -> {
                gen.map();
                gen.value( "errorCode", error.getErrorCode().toString() );
                gen.value( "message", error.getMessage() );
                gen.value( "i18n", error.getI18n() );

                if ( error instanceof AttachmentValidationError attachmentError )
                {
                    gen.value( "attachment", attachmentError.getAttachment().toString() );
                }

                if ( error instanceof DataValidationError dataError )
                {
                    gen.value( "propertyPath", dataError.getPropertyPath().toString() );
                }

                final List<Object> args = error.getArgs();
                if ( args != null && !args.isEmpty() )
                {
                    gen.array( "args" );
                    args.forEach( gen::value );
                    gen.end();
                }

                gen.end();
            } );
        }

        gen.end();
    }
}
