package com.enonic.wem.api.schema.mixin;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;

import com.enonic.wem.api.form.FieldSetXml;
import com.enonic.wem.api.form.FormItem;
import com.enonic.wem.api.form.FormItemSetXml;
import com.enonic.wem.api.form.FormItemXml;
import com.enonic.wem.api.form.FormItemXmlHelper;
import com.enonic.wem.api.form.InputXml;
import com.enonic.wem.xml.XmlObject;

@XmlRootElement(name = "mixin")
public class MixinXml
    implements XmlObject<Mixin, Mixin.Builder>
{
    @XmlElement(name = "display-name", required = false)
    private String displayName;


    @XmlElements({@XmlElement(name = "input", type = InputXml.class), @XmlElement(name = "form-item-set", type = FormItemSetXml.class),
                     @XmlElement(name = "field-set", type = FieldSetXml.class)})
    @XmlElementWrapper(name = "items")
    private List<FormItemXml> formItems = new ArrayList<>();

    @Override
    public void from( final Mixin mixin )
    {
        this.displayName = mixin.getDisplayName();

        for ( final FormItem formItem : mixin.getFormItems() )
        {
            formItems.add( FormItemXmlHelper.fromFormItem( formItem ) );
        }
    }

    @Override
    public void to( final Mixin.Builder builder )
    {
        builder
            .displayName( displayName );

        for ( final FormItemXml formItemXml : formItems )
        {
            final FormItem formItem = FormItemXmlHelper.toFormItem( formItemXml );
            builder.addFormItem( formItem );
        }
    }

}
