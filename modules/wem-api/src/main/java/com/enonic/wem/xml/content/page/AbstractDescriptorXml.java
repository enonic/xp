package com.enonic.wem.xml.content.page;

import javax.xml.bind.annotation.XmlElement;

import com.enonic.wem.api.content.page.Descriptor;
import com.enonic.wem.api.form.Form;
import com.enonic.wem.api.form.FormXml;
import com.enonic.wem.xml.XmlObject;

abstract class AbstractDescriptorXml<I, O>
    implements XmlObject<I, O>
{
    @XmlElement(name = "display-name", required = false)
    private String displayName;

    @XmlElement(name = "config", required = false)
    private FormXml configForm = new FormXml();

    protected void fromDescriptor( final Descriptor descriptor )
    {
        this.displayName = descriptor.getDisplayName();
        this.configForm.from( descriptor.getConfigForm() );
    }

    protected void toDescriptor( final Descriptor.BaseDescriptorBuilder builder )
    {
        builder.displayName( this.displayName );
        final Form.Builder formBuilder = Form.newForm();
        this.configForm.to( formBuilder );
        builder.config( formBuilder.build() );
    }
}
