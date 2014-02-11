package com.enonic.wem.api.form.inputtype;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "config")
public class TextAreaConfigXml
    extends ConfigXml<TextAreaConfig, TextAreaConfig.Builder>
{
    @XmlElement(name = "rows", required = true)
    private int rows;

    @XmlElement(name = "columns", required = true)
    private int columns;

    @Override
    public void from( final TextAreaConfig input )
    {
        this.rows = input.getRows();
        this.columns = input.getColumns();
    }

    @Override
    public void to( final TextAreaConfig.Builder output )
    {
        output.rows( this.rows );
        output.columns( this.columns );
    }
}
