package com.enonic.wem.api.command.content;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.content.data.DataSet;
import com.enonic.wem.api.content.type.ContentType;
import com.enonic.wem.api.content.type.validator.DataValidationErrors;

public class ValidateDataSet
    extends Command<DataValidationErrors>

{
    private DataSet dataSet;

    private ContentType contentType;

    public DataSet getDataSet()
    {
        return dataSet;
    }

    public ValidateDataSet dataSet( final DataSet dataSet )
    {
        this.dataSet = dataSet;
        return this;
    }

    public ContentType getContentType()
    {
        return contentType;
    }

    public ValidateDataSet contentType( final ContentType contentType )
    {
        this.contentType = contentType;
        return this;
    }

    @Override
    public void validate()
    {
        Preconditions.checkNotNull( dataSet, "dataSet cannot be null" );
        Preconditions.checkNotNull( contentType, "contentType cannot be null" );
    }
}
