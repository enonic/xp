package com.enonic.wem.xml.form;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import com.enonic.wem.api.form.Occurrences;
import com.enonic.wem.xml.XmlObject;

@XmlRootElement(name = "occurrences")
public final class OccurrencesXml
    implements XmlObject<Occurrences, Occurrences.Builder>
{
    @XmlAttribute(name = "minimum", required = true)
    private int minimum;

    @XmlAttribute(name = "maximum", required = true)
    private int maximum;

    @Override
    public void from( final Occurrences input )
    {
        this.minimum = input.getMinimum();
        this.maximum = input.getMinimum();
    }

    @Override
    public void to( final Occurrences.Builder output )
    {
        output.minimum( this.minimum );
        output.maximum( this.maximum );
    }
}
