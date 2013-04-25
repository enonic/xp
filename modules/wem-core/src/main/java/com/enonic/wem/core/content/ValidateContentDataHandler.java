package com.enonic.wem.core.content;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.command.content.ValidateContentData;
import com.enonic.wem.api.content.data.ContentData;
import com.enonic.wem.api.content.schema.content.ContentType;
import com.enonic.wem.api.content.schema.content.QualifiedContentTypeName;
import com.enonic.wem.api.content.schema.content.QualifiedContentTypeNames;
import com.enonic.wem.api.content.schema.content.validator.DataValidationErrors;
import com.enonic.wem.api.content.schema.content.validator.OccurrenceValidator;
import com.enonic.wem.core.command.CommandContext;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.content.schema.content.dao.ContentTypeDao;

@Component
public final class ValidateContentDataHandler
    extends CommandHandler<ValidateContentData>
{
    private ContentTypeDao contentTypeDao;

    public ValidateContentDataHandler()
    {
        super( ValidateContentData.class );
    }

    @Override
    public void handle( final CommandContext context, final ValidateContentData command )
        throws Exception
    {
        final ContentData contentData = command.getContentData();
        final QualifiedContentTypeName qualifiedContentTypeName = command.getContentType();
        final ContentType contentType =
            contentTypeDao.select( QualifiedContentTypeNames.from( qualifiedContentTypeName ), context.getJcrSession() ).first();
        Preconditions.checkArgument( contentType != null, "ContentType [%s] not found", qualifiedContentTypeName );

        final OccurrenceValidator occurrenceValidator = new OccurrenceValidator( contentType );

        final DataValidationErrors validationErrors = occurrenceValidator.validate( contentData );
        command.setResult( validationErrors );
    }

    @Inject
    public void setContentTypeDao( final ContentTypeDao contentTypeDao )
    {
        this.contentTypeDao = contentTypeDao;
    }
}
