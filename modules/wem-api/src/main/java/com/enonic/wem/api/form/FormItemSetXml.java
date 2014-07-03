package com.enonic.wem.api.form;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;

import com.enonic.wem.api.xml.XmlObject;

@Deprecated
@XmlRootElement(name = "form-item-set")
public final class FormItemSetXml
    implements XmlObject<FormItemSet, FormItemSet.Builder>, FormItemXml
{
    @XmlAttribute(name = "name", required = true)
    private String name;

    @XmlElement(name = "label", required = false)
    private String label;

    @XmlElements({@XmlElement(name = "input", type = InputXml.class), @XmlElement(name = "form-item-set", type = FormItemSetXml.class),
                     @XmlElement(name = "field-set", type = FieldSetXml.class),
                     @XmlElement(name = "mixin-reference", type = MixinReferenceXml.class)})
    @XmlElementWrapper(name = "items")
    private List<FormItemXml> formItems = new ArrayList<>();

    @XmlElement(name = "immutable", required = false)
    private boolean immutable;

    @XmlElement(name = "occurrences")
    private OccurrencesXml occurrences = new OccurrencesXml();

    @XmlElement(name = "custom-text", required = false)
    private String customText;

    @XmlElement(name = "help-text", required = false)
    private String helpText;

    @Override
    public void from( final FormItemSet input )
    {
        this.name = input.getName();
        this.label = input.getLabel();
        this.immutable = input.isImmutable();
        this.occurrences.from( input.getOccurrences() );
        this.customText = input.getCustomText();
        this.helpText = input.getHelpText();

        FormItemXmlHelper.fromFormItem( this.formItems, input );
    }

    @Override
    public void to( final FormItemSet.Builder output )
    {
        final Occurrences.Builder occurrences = Occurrences.newOccurrences();
        this.occurrences.to( occurrences );
        output.name( this.name ).
            label( this.label ).
            immutable( this.immutable ).
            occurrences( occurrences.build() ).
            customText( this.customText ).
            helpText( this.helpText );

        for ( FormItemXml formItemXml : formItems )
        {
            final FormItem formItem = FormItemXmlHelper.toFormItem( formItemXml );
            output.addFormItem( formItem );
        }
    }

}
