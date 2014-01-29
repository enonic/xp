package com.enonic.wem.api.form;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;

import com.enonic.wem.xml.XmlObject;

@XmlRootElement(name = "form")
public class FormXml
    implements XmlObject<Form, Form.Builder>
{
    @XmlElements({@XmlElement(name = "input", type = InputXml.class), @XmlElement(name = "form-item-set", type = FormItemSetXml.class),
                     @XmlElement(name = "field-set", type = FieldSetXml.class),
                     @XmlElement(name = "mixin-reference", type = MixinReferenceXml.class)})
    private List<FormItemXml> formItems = new ArrayList<>();

    @Override
    public void from( final Form input )
    {
        FormItemXmlHelper.fromFormItem( this.formItems, input );
    }

    @Override
    public void to( final Form.Builder output )
    {
        for ( FormItemXml formItemXml : formItems )
        {
            final FormItem formItem = FormItemXmlHelper.toFormItem( formItemXml );
            output.addFormItem( formItem );
        }
    }

}
