package com.enonic.wem.core.content;

import org.springframework.stereotype.Component;

import com.enonic.wem.api.command.content.ValidateContentData;
import com.enonic.wem.api.content.data.ContentData;
import com.enonic.wem.api.content.type.ContentType;
import com.enonic.wem.api.content.type.validator.DataValidationErrors;
import com.enonic.wem.api.content.type.validator.OccurrenceValidator;
import com.enonic.wem.core.command.CommandContext;
import com.enonic.wem.core.command.CommandHandler;

@Component
public final class ValidateContentDataHandler
    extends CommandHandler<ValidateContentData>
{

    public ValidateContentDataHandler()
    {
        super( ValidateContentData.class );
    }

    @Override
    public void handle( final CommandContext context, final ValidateContentData command )
        throws Exception
    {
        final ContentData contentData = command.getContentData();
        final ContentType contentType = command.getContentType();
        final OccurrenceValidator occurrenceValidator = new OccurrenceValidator( contentType );

        final DataValidationErrors validationErrors = occurrenceValidator.validate( contentData );
        command.setResult( validationErrors );
    }

}
