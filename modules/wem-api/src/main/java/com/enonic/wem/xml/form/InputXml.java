package com.enonic.wem.xml.form;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.enonic.wem.api.form.Input;
import com.enonic.wem.api.form.Occurrences;
import com.enonic.wem.api.form.inputtype.InputTypes;
import com.enonic.wem.xml.XmlObject;

@XmlRootElement(name = "input")
public final class InputXml
    implements XmlObject<Input, Input.Builder>, FormItemXml
{
    @XmlAttribute(name = "name", required = true)
    private String name;

    @XmlAttribute(name = "type", required = true)
    private String type;

    @XmlElement(name = "label", required = false)
    private String label;

    @XmlElement(name = "immutable", required = false)
    private Boolean immutable;

    @XmlElement(name = "indexed", required = false)
    private Boolean indexed;

    @XmlElement(name = "custom-text", required = false)
    private String customText;

    private OccurrencesXml occurrences = new OccurrencesXml();

    @XmlElement(name = "help-text", required = false)
    private String helpText;

    @XmlElement(name = "validation-regexp", required = false)
    private String validationRegexp;

    @Override
    public void from( final Input input )
    {
        this.name = input.getName();
        this.type = input.getInputType().getName();
        this.label = input.getLabel();
        this.immutable = input.isImmutable();
        this.indexed = input.isIndexed();
        this.customText = input.getCustomText();
        this.occurrences.from( input.getOccurrences() );
        this.helpText = input.getHelpText();
        this.validationRegexp = input.getValidationRegexp() != null ? input.getValidationRegexp().toString() : null;
    }

    @Override
    public void to( final Input.Builder output )
    {
        final Occurrences.Builder occurrences = Occurrences.newOccurrences();
        this.occurrences.to( occurrences );
        output.name( this.name ).
            inputType( InputTypes.parse( this.type ) ).
            label( this.label ).
            immutable( this.immutable ).
            indexed( this.indexed ).
            customText( this.customText ).
            occurrences( occurrences.build() ).
            helpText( this.helpText ).
            validationRegexp( this.validationRegexp );
    }
}
