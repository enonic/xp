package com.enonic.wem.core.content;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.command.content.ValidateRootDataSet;
import com.enonic.wem.api.content.data.RootDataSet;
import com.enonic.wem.api.content.schema.content.ContentType;
import com.enonic.wem.api.content.schema.content.QualifiedContentTypeName;
import com.enonic.wem.api.content.schema.content.QualifiedContentTypeNames;
import com.enonic.wem.api.content.schema.content.validator.DataValidationErrors;
import com.enonic.wem.api.content.schema.content.validator.OccurrenceValidator;
import com.enonic.wem.core.command.CommandContext;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.content.schema.content.dao.ContentTypeDao;

@Component
public final class ValidateRootDataSetHandler
    extends CommandHandler<ValidateRootDataSet>
{
    private ContentTypeDao contentTypeDao;

    public ValidateRootDataSetHandler()
    {
        super( ValidateRootDataSet.class );
    }

    @Override
    public void handle( final CommandContext context, final ValidateRootDataSet command )
        throws Exception
    {
        final RootDataSet rootDataSet = command.getRootDataSet();
        final QualifiedContentTypeName qualifiedContentTypeName = command.getContentType();
        final ContentType contentType =
            contentTypeDao.select( QualifiedContentTypeNames.from( qualifiedContentTypeName ), context.getJcrSession() ).first();
        Preconditions.checkArgument( contentType != null, "ContentType [%s] not found", qualifiedContentTypeName );

        final OccurrenceValidator occurrenceValidator = new OccurrenceValidator( contentType );

        final DataValidationErrors validationErrors = occurrenceValidator.validate( rootDataSet );
        command.setResult( validationErrors );
    }

    @Inject
    public void setContentTypeDao( final ContentTypeDao contentTypeDao )
    {
        this.contentTypeDao = contentTypeDao;
    }
}
