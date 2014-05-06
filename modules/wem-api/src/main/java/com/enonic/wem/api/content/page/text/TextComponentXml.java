package com.enonic.wem.api.content.page.text;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import com.enonic.wem.api.content.page.AbstractPageComponentXml;
import com.enonic.wem.api.content.page.PageComponent;
import com.enonic.wem.api.xml.XmlObject;

import static com.enonic.wem.api.content.page.text.TextComponent.newTextComponent;

@XmlRootElement(name = "text-component")
public final class TextComponentXml
    extends AbstractPageComponentXml
    implements XmlObject<TextComponent, TextComponent.Builder>
{

    @XmlAttribute(name = "text", required = false)
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
        builder.text( text );
    }

    public PageComponent toPageComponent()
    {
        TextComponent.Builder builder = newTextComponent();
        to( builder );
        return builder.build();
    }
}
