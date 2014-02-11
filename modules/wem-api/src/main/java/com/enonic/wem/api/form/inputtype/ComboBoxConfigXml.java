package com.enonic.wem.api.form.inputtype;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "config")
public class ComboBoxConfigXml
    extends ConfigXml<ComboBoxConfig, ComboBoxConfig.Builder>
{
    @XmlElementWrapper(name = "options")
    @XmlElementRef(name = "options", type = OptionXml.class)
    private List<OptionXml> options = new ArrayList<>();

    @Override
    public void from( final ComboBoxConfig input )
    {
        for ( Option option : input.getOptions() )
        {
            final OptionXml optionXml = new OptionXml();
            optionXml.from( option );

            options.add( optionXml );
        }
    }

    @Override
    public void to( final ComboBoxConfig.Builder output )
    {
        for ( OptionXml option : options )
        {
            option.to( output );
        }
    }
}
