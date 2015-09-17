package com.enonic.xp.admin.impl.json.content.page.region;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.xp.region.ComponentName;
import com.enonic.xp.region.TextComponent;

@SuppressWarnings("UnusedDeclaration")
public class TextComponentJson
    extends ComponentJson<TextComponent>
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
        super( TextComponent.create().
            name( name != null ? ComponentName.from( name ) : null ).
            text( text ).
            build() );

        this.text = getComponent();
    }

    public String getText()
    {
        return this.text.getText();
    }
}
