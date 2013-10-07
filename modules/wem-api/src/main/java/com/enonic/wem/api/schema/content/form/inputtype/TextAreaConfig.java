package com.enonic.wem.api.schema.content.form.inputtype;

import com.enonic.wem.api.data.Property;
import com.enonic.wem.api.schema.content.form.InvalidValueException;

public class TextAreaConfig
    implements InputTypeConfig
{

    private int rows;

    private int columns;

    private TextAreaConfig()
    {

    }

    public int getRows()
    {
        return rows;
    }

    public int getColumns()
    {
        return columns;
    }

    @Override
    public void checkValidity( final Property property )
        throws InvalidValueException
    {

    }

    public static Builder newTextAreaConfig()
    {
        return new Builder();
    }


    public static class Builder
    {

        private TextAreaConfig textAreaConfig;

        Builder()
        {
            textAreaConfig = new TextAreaConfig();
        }

        public Builder rows( int rows )
        {
            textAreaConfig.rows = rows;
            return this;
        }

        public Builder columns( int columns )
        {
            textAreaConfig.columns = columns;
            return this;
        }

        public TextAreaConfig build()
        {
            return textAreaConfig;
        }
    }
}
