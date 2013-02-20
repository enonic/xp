package com.enonic.wem.api.command.content;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.content.data.RootDataSet;
import com.enonic.wem.api.content.schema.type.ContentType;
import com.enonic.wem.api.content.schema.type.validator.DataValidationErrors;

public class ValidateRootDataSet
    extends Command<DataValidationErrors>

{
    private RootDataSet rootDataSet;

    private ContentType contentType;

    public RootDataSet getRootDataSet()
    {
        return rootDataSet;
    }

    public ValidateRootDataSet rootDataSet( final RootDataSet rootDataSet )
    {
        this.rootDataSet = rootDataSet;
        return this;
    }

    public ContentType getContentType()
    {
        return contentType;
    }

    public ValidateRootDataSet contentType( final ContentType contentType )
    {
        this.contentType = contentType;
        return this;
    }

    @Override
    public void validate()
    {
        Preconditions.checkNotNull( rootDataSet, "rootDataSet cannot be null" );
        Preconditions.checkNotNull( contentType, "contentType cannot be null" );
    }
}
