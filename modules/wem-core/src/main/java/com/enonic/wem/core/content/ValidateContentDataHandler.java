package com.enonic.wem.core.content;

import org.springframework.stereotype.Component;

import com.enonic.wem.api.command.content.ValidateRootDataSet;
import com.enonic.wem.api.content.data.RootDataSet;
import com.enonic.wem.api.content.schema.type.ContentType;
import com.enonic.wem.api.content.schema.type.validator.DataValidationErrors;
import com.enonic.wem.api.content.schema.type.validator.OccurrenceValidator;
import com.enonic.wem.core.command.CommandContext;
import com.enonic.wem.core.command.CommandHandler;

@Component
public final class ValidateContentDataHandler
    extends CommandHandler<ValidateRootDataSet>
{

    public ValidateContentDataHandler()
    {
        super( ValidateRootDataSet.class );
    }

    @Override
    public void handle( final CommandContext context, final ValidateRootDataSet command )
        throws Exception
    {
        final RootDataSet rootDataSet = command.getRootDataSet();
        final ContentType contentType = command.getContentType();
        final OccurrenceValidator occurrenceValidator = new OccurrenceValidator( contentType );

        final DataValidationErrors validationErrors = occurrenceValidator.validate( rootDataSet );
        command.setResult( validationErrors );
    }

}
