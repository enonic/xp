package com.enonic.wem.core.entity.relationship;


import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import com.enonic.wem.api.entity.EntityIndexConfig;
import com.enonic.wem.core.entity.EntityPatternIndexConfigJson;
import com.enonic.wem.core.entity.EntityPropertyIndexConfigJson;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.WRAPPER_OBJECT)
@JsonSubTypes({@JsonSubTypes.Type(value = EntityPatternIndexConfigJson.class, name = "patternConfig"),
                  @JsonSubTypes.Type(value = EntityPropertyIndexConfigJson.class, name = "propertyConfig")})

public abstract class EntityIndexConfigJson
{
    private final String analyzer;

    private final String collection;

    public EntityIndexConfigJson( final String analyzer, final String collection )
    {
        this.analyzer = analyzer;
        this.collection = collection;
    }


    public abstract EntityIndexConfig toEntityIndexConfig();

    @SuppressWarnings("UnusedDeclaration")
    public String getAnalyzer()
    {
        return analyzer;
    }

    @SuppressWarnings("UnusedDeclaration")
    public String getCollection()
    {
        return collection;
    }

}
