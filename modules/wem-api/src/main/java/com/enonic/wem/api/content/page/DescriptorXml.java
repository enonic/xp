package com.enonic.wem.api.content.page;

import javax.xml.bind.annotation.XmlElement;

import com.enonic.wem.api.form.Form;
import com.enonic.wem.api.form.FormXml;
import com.enonic.wem.api.xml.XmlObject;

public abstract class DescriptorXml<I, O>
implements XmlObject<I, O>
{
    @XmlElement(name = "display-name", required = false)
    private String displayName;

    @XmlElement(name = "config", required = false)
    private FormXml configForm = new FormXml();

    protected void fromDescriptor( final Descriptor descriptor )
    {
        this.displayName = descriptor.getDisplayName();
        this.configForm.from( descriptor.getConfig() );
    }

    protected void toDescriptor( final Descriptor.BaseDescriptorBuilder builder )
    {
        builder.displayName( this.displayName );
        final Form.Builder formBuilder = Form.newForm();
        this.configForm.to( formBuilder );
        builder.config( formBuilder.build() );
    }
}
