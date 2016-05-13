package com.enonic.xp.admin.impl.rest.resource.macro.json;


import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.xp.content.ContentPath;
import com.enonic.xp.data.PropertyArrayJson;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.data.PropertyTreeJson;
import com.enonic.xp.macro.MacroKey;

public final class PreviewMacroJson
{

    private final PropertyTree formData;

    private final MacroKey macroKey;

    private final ContentPath contentPath;

    @JsonCreator
    public PreviewMacroJson( @JsonProperty("macroKey") final String macroKeyStr,
                             @JsonProperty("form") final List<PropertyArrayJson> macroForm,
                             @JsonProperty("contentPath") final String contentPathStr )
    {
        formData = PropertyTreeJson.fromJson( macroForm );
        macroKey = MacroKey.from( macroKeyStr );
        contentPath = ContentPath.from( contentPathStr );
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

    @JsonIgnore
    public ContentPath getContentPath()
    {
        return contentPath;
    }
}
