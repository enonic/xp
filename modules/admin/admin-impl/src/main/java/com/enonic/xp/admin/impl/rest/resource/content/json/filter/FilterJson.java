package com.enonic.xp.admin.impl.rest.resource.content.json.filter;


import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import com.enonic.xp.query.filter.Filter;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.WRAPPER_OBJECT)
@JsonSubTypes({                                                                                                   //
                  @JsonSubTypes.Type(value = BooleanFilterJson.class, name = "BooleanFilter"),    //
                  @JsonSubTypes.Type(value = RangeFilterJson.class, name = "RangeFilter") //
              })

public abstract class FilterJson
{
    public abstract Filter getFilter();
}
