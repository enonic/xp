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

    private final boolean decideFulltextByValueType;

    public EntityIndexConfigJson( final String analyzer, final String collection, final boolean decideFulltextByValueType )
    {
        this.analyzer = analyzer;
        this.collection = collection;
        this.decideFulltextByValueType = decideFulltextByValueType;
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

    public boolean isDecideFulltextByValueType()
    {
        return decideFulltextByValueType;
    }
}
