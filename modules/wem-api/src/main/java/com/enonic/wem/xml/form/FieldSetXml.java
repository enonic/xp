package com.enonic.wem.xml.form;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;

import com.enonic.wem.api.form.FieldSet;
import com.enonic.wem.api.form.FormItem;
import com.enonic.wem.xml.XmlObject;

@XmlRootElement(name = "field-set")
public final class FieldSetXml
    implements XmlObject<FieldSet, FieldSet.Builder>, FormItemXml
{
    @XmlAttribute(name = "name", required = true)
    private String name;

    @XmlElement(name = "label", required = false)
    private String label;

    @XmlElements({@XmlElement(name = "input", type = InputXml.class), @XmlElement(name = "form-item-set", type = FormItemSetXml.class),
                     @XmlElement(name = "field-set", type = FieldSetXml.class)})
    @XmlElementWrapper(name = "items")
    private List<FormItemXml> formItems = new ArrayList<>();

    @Override
    public void from( final FieldSet input )
    {
        this.label = input.getLabel();
        this.name = input.getName();

        FormItemsHelper.fromFormItems( this.formItems, input );
    }

    @Override
    public void to( final FieldSet.Builder output )
    {
        output.label( this.label ).name( this.name );

        for ( FormItemXml formItemXml : formItems )
        {
            final FormItem formItem = FormItemsHelper.toFormItem( formItemXml );
            output.addFormItem( formItem );
        }
    }

}
