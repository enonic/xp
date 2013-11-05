package com.enonic.wem.api.command.content;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.content.data.ContentData;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.schema.content.validator.DataValidationErrors;

public class ValidateContentData
    extends Command<DataValidationErrors>

{
    private ContentData contentData;

    private ContentTypeName contentType;

    public ContentData getContentData()
    {
        return contentData;
    }

    public ValidateContentData contentData( final ContentData contentData )
    {
        this.contentData = contentData;
        return this;
    }

    public ContentTypeName getContentType()
    {
        return contentType;
    }

    public ValidateContentData contentType( final ContentTypeName contentType )
    {
        this.contentType = contentType;
        return this;
    }

    @Override
    public void validate()
    {
        Preconditions.checkNotNull( contentData, "contentData cannot be null" );
        Preconditions.checkNotNull( contentType, "contentType cannot be null" );
    }
}
