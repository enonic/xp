package com.enonic.wem.core.content;

import javax.inject.Inject;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.command.content.ValidateContentData;
import com.enonic.wem.api.content.data.ContentData;
import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.schema.content.ContentTypeService;
import com.enonic.wem.api.schema.content.GetContentTypeParams;
import com.enonic.wem.api.schema.content.validator.DataValidationErrors;
import com.enonic.wem.api.schema.content.validator.OccurrenceValidator;
import com.enonic.wem.core.command.CommandHandler;


public final class ValidateContentDataHandler
    extends CommandHandler<ValidateContentData>
{
    private ContentTypeService contentTypeService;

    @Override
    public void handle()
        throws Exception
    {
        final ContentData contentData = command.getContentData();
        final ContentTypeName contentTypeName = command.getContentType();
        final ContentType contentType = contentTypeService.getByName( new GetContentTypeParams().contentTypeName( contentTypeName ) );

        Preconditions.checkArgument( contentType != null, "ContentType [%s] not found", contentTypeName );

        final OccurrenceValidator occurrenceValidator = new OccurrenceValidator( contentType );

        final DataValidationErrors validationErrors = occurrenceValidator.validate( contentData );
        command.setResult( validationErrors );
    }

    @Inject
    public void setContentTypeService( final ContentTypeService contentTypeService )
    {
        this.contentTypeService = contentTypeService;
    }
}
