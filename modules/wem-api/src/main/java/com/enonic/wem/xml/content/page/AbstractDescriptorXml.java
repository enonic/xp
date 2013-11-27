package com.enonic.wem.xml.content.page;

import javax.xml.bind.annotation.XmlElement;

import com.enonic.wem.api.content.page.BaseDescriptor;
import com.enonic.wem.api.form.Form;
import com.enonic.wem.api.module.ModuleResourceKey;
import com.enonic.wem.xml.XmlObject;
import com.enonic.wem.xml.form.FormXml;

abstract class AbstractDescriptorXml<I, O>
    implements XmlObject<I, O>
{
    @XmlElement(name = "display-name", required = false)
    private String displayName;

    @XmlElement(name = "controller", required = false)
    private String controller;

    @XmlElement(name = "config", required = false)
    private FormXml configForm = new FormXml();

    protected void fromDescriptor( final BaseDescriptor descriptor )
    {
        this.displayName = descriptor.getDisplayName();
        this.controller = descriptor.getControllerResource().toString();
        this.configForm.from( descriptor.getConfig() );
    }

    protected void toDescriptor( final BaseDescriptor.BaseDescriptorBuilder builder )
    {
        builder.displayName( this.displayName );
        builder.controllerResource( ModuleResourceKey.from( this.controller ) );
        final Form.Builder formBuilder = Form.newForm();
        this.configForm.to( formBuilder );
        builder.config( formBuilder.build() );
    }
}
