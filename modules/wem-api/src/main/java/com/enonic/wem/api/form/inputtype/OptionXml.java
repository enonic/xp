package com.enonic.wem.api.form.inputtype;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.enonic.wem.xml.XmlObject;

@XmlRootElement(name = "option")
public class OptionXml
    implements XmlObject<Option, OptionBuilder>
{
    @XmlElement(name = "label", required = false)
    private String label;

    @XmlElement(name = "value", required = true)
    private String value;

    @Override
    public void from( final Option input )
    {
        this.label = input.getLabel();
        this.value = input.getValue();
    }

    @Override
    public void to( final OptionBuilder output )
    {
        output.addOption( label, value );
    }
}
