package com.enonic.wem.core.schema.content;

import com.enonic.wem.api.command.schema.content.ValidateContentType;
import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.validator.ContentTypeSuperTypeValidator;
import com.enonic.wem.core.command.CommandHandler;


public class ValidateContentTypeHandler
    extends CommandHandler<ValidateContentType>
{
    @Override
    public void handle()
        throws Exception
    {
        ContentType contentType = command.getContentType();

        ContentTypeSuperTypeValidator validator = ContentTypeSuperTypeValidator.newContentTypeSuperTypeValidator().
            client( context.getClient() ).
            build();

        validator.validate( contentType.getContentTypeName(), contentType.getSuperType() );
        command.setResult( validator.getResult() );
    }

}
