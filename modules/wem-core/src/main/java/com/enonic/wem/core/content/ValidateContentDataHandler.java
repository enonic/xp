package com.enonic.wem.core.content;

import org.springframework.stereotype.Component;

import com.enonic.wem.api.command.content.ValidateDataSet;
import com.enonic.wem.api.content.data.DataSet;
import com.enonic.wem.api.content.type.ContentType;
import com.enonic.wem.api.content.type.validator.DataValidationErrors;
import com.enonic.wem.api.content.type.validator.OccurrenceValidator;
import com.enonic.wem.core.command.CommandContext;
import com.enonic.wem.core.command.CommandHandler;

@Component
public final class ValidateContentDataHandler
    extends CommandHandler<ValidateDataSet>
{

    public ValidateContentDataHandler()
    {
        super( ValidateDataSet.class );
    }

    @Override
    public void handle( final CommandContext context, final ValidateDataSet command )
        throws Exception
    {
        final DataSet rootDataSet = command.getDataSet();
        final ContentType contentType = command.getContentType();
        final OccurrenceValidator occurrenceValidator = new OccurrenceValidator( contentType );

        final DataValidationErrors validationErrors = occurrenceValidator.validate( rootDataSet );
        command.setResult( validationErrors );
    }

}
