package com.enonic.wem.api.content.page.text;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import com.enonic.wem.api.content.page.DescriptorKey;
import com.enonic.wem.api.content.page.PageComponentXml;
import com.enonic.wem.xml.XmlObject;

@XmlRootElement(name = "text-component")
public final class TextComponentXml
    extends PageComponentXml
    implements XmlObject<TextComponent, TextComponent.Builder>
{
    @XmlAttribute(name = "text", required = true)
    String text;

    @Override
    public void from( final TextComponent component )
    {
        super.from( component );
        this.text = component.getText();
    }

    @Override
    public void to( final TextComponent.Builder builder )
    {
        super.to( builder );
        builder.text( this.text );
    }

    @Override
    protected DescriptorKey toDescriptorKey( final String s )
    {
        return TextDescriptorKey.from( s );
    }
}
