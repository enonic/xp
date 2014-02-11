package com.enonic.wem.api.form.inputtype;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "config")
public class SingleSelectorConfigXml
    extends ConfigXml<SingleSelectorConfig, SingleSelectorConfig.Builder>
{
    @XmlElementWrapper(name = "options")
    @XmlElementRef(name = "options", type = OptionXml.class)
    private List<OptionXml> options = new ArrayList<>();

    @XmlElement(name = "selector-type", required = false)
    private String type;

    @Override
    public void from( final SingleSelectorConfig input )
    {
        this.type = input.getType().toString();

        for ( Option option : input.getOptions() )
        {
            final OptionXml optionXml = new OptionXml();
            optionXml.from( option );

            this.options.add( optionXml );
        }
    }

    @Override
    public void to( final SingleSelectorConfig.Builder output )
    {
        output.type( SingleSelectorConfig.SelectorType.valueOf( type ) );

        for ( OptionXml option : options )
        {
            option.to( output );
        }
    }
}
