package com.enonic.wem.api.form;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.enonic.wem.api.xml.XmlObject;

@Deprecated
@XmlRootElement(name = "mixin-reference")
public final class MixinReferenceXml
    implements XmlObject<MixinReference, MixinReference.Builder>, FormItemXml
{
    @XmlAttribute(name = "name", required = true)
    private String name;

    @XmlElement(name = "reference", required = true)
    private String reference;

    @Override
    public void from( final MixinReference input )
    {
        this.name = input.getName();
        this.reference = input.getMixinName().toString();
    }

    @Override
    public void to( final MixinReference.Builder output )
    {
        output.name( name ).mixin( reference );
    }
}
