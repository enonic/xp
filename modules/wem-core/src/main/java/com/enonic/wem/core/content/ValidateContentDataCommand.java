package com.enonic.wem.core.content;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.content.ValidateContentData;
import com.enonic.wem.api.data2.PropertyTree;
import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.schema.content.ContentTypeService;
import com.enonic.wem.api.schema.content.GetContentTypeParams;
import com.enonic.wem.api.schema.content.validator.DataValidationErrors;
import com.enonic.wem.api.schema.content.validator.OccurrenceValidator;

final class ValidateContentDataCommand
{
    private ContentTypeService contentTypeService;

    private ValidateContentData data;

    DataValidationErrors execute()
    {
        data.validate();

        return doExecute();
    }

    DataValidationErrors doExecute()
    {
        final PropertyTree contentData = this.data.getContentData();
        final ContentTypeName contentTypeName = this.data.getContentType();
        final ContentType contentType = contentTypeService.getByName( new GetContentTypeParams().contentTypeName( contentTypeName ) );

        Preconditions.checkArgument( contentType != null, "ContentType [%s] not found", contentTypeName );

        final OccurrenceValidator occurrenceValidator = new OccurrenceValidator( contentType );

        return occurrenceValidator.validate( contentData );
    }

    ValidateContentDataCommand contentTypeService( final ContentTypeService contentTypeService )
    {
        this.contentTypeService = contentTypeService;
        return this;
    }

    ValidateContentDataCommand data( final ValidateContentData data )
    {
        this.data = data;
        return this;
    }
}
