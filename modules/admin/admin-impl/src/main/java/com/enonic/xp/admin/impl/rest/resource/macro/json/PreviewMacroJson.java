package com.enonic.xp.admin.impl.rest.resource.macro.json;


import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.xp.content.ContentPath;
import com.enonic.xp.data.PropertyArrayJson;

public final class PreviewMacroJson
    extends PreviewStringMacroJson
{

    private final ContentPath contentPath;

    @JsonCreator
    public PreviewMacroJson( @JsonProperty("macroKey") final String macroKeyStr,
                             @JsonProperty("form") final List<PropertyArrayJson> macroForm,
                             @JsonProperty("contentPath") final String contentPathStr )
    {
        super( macroKeyStr, macroForm );
        contentPath = ContentPath.from( contentPathStr );
    }

    @JsonIgnore
    public ContentPath getContentPath()
    {
        return contentPath;
    }
}
