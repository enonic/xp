package com.enonic.xp.admin.impl.rest.resource.macro.json;


import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.xp.data.PropertyArrayJson;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.data.PropertyTreeJson;
import com.enonic.xp.macro.MacroKey;

public final class PreviewStringMacroJson
{
    private final PropertyTree formData;

    private final MacroKey macroKey;

    @JsonCreator
    public PreviewStringMacroJson( @JsonProperty("macroKey") final String macroKeyStr,
                                   @JsonProperty("form") final List<PropertyArrayJson> macroForm )
    {
        formData = PropertyTreeJson.fromJson( macroForm );
        macroKey = MacroKey.from( macroKeyStr );
    }

    @JsonIgnore
    public PropertyTree getFormData()
    {
        return formData;
    }

    @JsonIgnore
    public MacroKey getMacroKey()
    {
        return macroKey;
    }

}
