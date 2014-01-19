package com.enonic.wem.api.content.page.layout;

import javax.xml.bind.annotation.XmlRootElement;

import com.enonic.wem.api.content.page.PageComponentXml;
import com.enonic.wem.api.content.page.TemplateKey;
import com.enonic.wem.xml.XmlObject;

@XmlRootElement(name = "layout-component")
public final class LayoutComponentXml
    extends PageComponentXml
    implements XmlObject<LayoutComponent, LayoutComponent.Builder>
{

    @Override
    public void from( final LayoutComponent component )
    {
        super.from( component );
    }

    @Override
    public void to( final LayoutComponent.Builder builder )
    {
        super.to( builder );
    }

    @Override
    protected TemplateKey toTemplateKey( final String s )
    {
        return LayoutTemplateKey.from( s );
    }
}
