package com.enonic.wem.api.command.content;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.content.data.ContentData;
import com.enonic.wem.api.content.type.ContentType;
import com.enonic.wem.api.content.type.validator.DataValidationErrors;

public class ValidateContentData
    extends Command<DataValidationErrors>

{
    private ContentData contentData;

    private ContentType contentType;

    public ContentData getContentData()
    {
        return contentData;
    }

    public ValidateContentData contentData( final ContentData contentData )
    {
        this.contentData = contentData;
        return this;
    }

    public ContentType getContentType()
    {
        return contentType;
    }

    public ValidateContentData contentType( final ContentType contentType )
    {
        this.contentType = contentType;
        return this;
    }

    @Override
    public void validate()
    {
        Preconditions.checkNotNull( this.contentData, "Content data cannot be null" );
        Preconditions.checkNotNull( this.contentType, "Content type cannot be null" );
    }
}
