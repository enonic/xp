package com.enonic.wem.admin.json.content.page.text;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.wem.admin.json.content.page.PageComponentJson;
import com.enonic.wem.admin.json.data.DataJson;
import com.enonic.wem.admin.json.data.RootDataSetJson;
import com.enonic.wem.api.content.page.ComponentName;
import com.enonic.wem.api.content.page.text.TextComponent;
import com.enonic.wem.api.content.page.text.TextDescriptorKey;

import static com.enonic.wem.api.content.page.text.TextComponent.newTextComponent;

public class TextComponentJson
    extends PageComponentJson<TextComponent>
{
    private final TextComponent text;

    @JsonCreator
    public TextComponentJson( @JsonProperty("name") final String name,
                              @JsonProperty("descriptor") final String descriptor,
                              @JsonProperty("config") final List<DataJson> config,
                              @JsonProperty("text") final String text )
    {
        super( newTextComponent().
            name( ComponentName.from( name ) ).
            descriptor( descriptor != null ? TextDescriptorKey.from( descriptor ) : null ).
            text( text ).
            config( config != null ? new RootDataSetJson( config ).getRootDataSet() : null ).
            build() );

        this.text = getComponent();
    }

    public TextComponentJson( final TextComponent component )
    {
        super( component );
        this.text = component;
    }

    public String getText()
    {
        return text.getText();
    }
}
