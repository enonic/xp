package com.enonic.wem.api.content.page.part;

import javax.xml.bind.annotation.XmlRootElement;

import com.enonic.wem.api.content.page.PageComponentXml;
import com.enonic.wem.api.content.page.TemplateKey;
import com.enonic.wem.xml.XmlObject;

@XmlRootElement(name = "part-component")
public final class PartComponentXml
    extends PageComponentXml
    implements XmlObject<PartComponent, PartComponent.Builder>
{

    @Override
    public void from( final PartComponent component )
    {
        super.from( component );
    }

    @Override
    public void to( final PartComponent.Builder builder )
    {
        super.to( builder );
    }

    @Override
    protected TemplateKey toTemplateKey( final String s )
    {
        return PartTemplateKey.from( s );
    }
}
