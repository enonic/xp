package com.enonic.wem.api.content.page.text;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.wem.api.content.page.AbstractPageComponentJson;
import com.enonic.wem.api.content.page.ComponentName;

import static com.enonic.wem.api.content.page.text.TextComponent.newTextComponent;

@SuppressWarnings("UnusedDeclaration")
public class TextComponentJson
    extends AbstractPageComponentJson<TextComponent>
{
    private final TextComponent text;

    public TextComponentJson( final TextComponent component )
    {
        super( component );
        this.text = component;
    }

    @JsonCreator
    public TextComponentJson( @JsonProperty("name") final String name, @JsonProperty("text") final String text )
    {
        super( newTextComponent().
            name( ComponentName.from( name ) ).
            text( text ).
            build() );

        this.text = getComponent();
    }

    public String getText()
    {
        return this.text.getText();
    }
}
