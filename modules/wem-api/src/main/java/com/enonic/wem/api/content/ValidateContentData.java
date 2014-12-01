package com.enonic.wem.api.content;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.data.PropertyTree;
import com.enonic.wem.api.schema.content.ContentTypeName;

public class ValidateContentData
{
    private PropertyTree contentData;

    private ContentTypeName contentType;

    public PropertyTree getContentData()
    {
        return contentData;
    }

    public ValidateContentData contentData( final PropertyTree contentData )
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

    public void validate()
    {
        Preconditions.checkNotNull( contentData, "data cannot be null" );
        Preconditions.checkNotNull( contentType, "contentType cannot be null" );
    }
}
