package com.enonic.wem.core.entity.relationship;


import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import com.enonic.wem.api.entity.IndexConfigDocumentOldShit;
import com.enonic.wem.core.entity.EntityPatternIndexConfigJson;
import com.enonic.wem.core.entity.EntityPropertyIndexConfigJson;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.WRAPPER_OBJECT)
@JsonSubTypes({@JsonSubTypes.Type(value = EntityPatternIndexConfigJson.class, name = "patternConfig"),
                  @JsonSubTypes.Type(value = EntityPropertyIndexConfigJson.class, name = "propertyConfig")})
public abstract class EntityIndexConfigJson
{
    private final String analyzer;

    public EntityIndexConfigJson( final String analyzer )
    {
        this.analyzer = analyzer;
    }

    public abstract IndexConfigDocumentOldShit toEntityIndexConfig();

    @SuppressWarnings("UnusedDeclaration")
    public String getAnalyzer()
    {
        return analyzer;
    }

}
