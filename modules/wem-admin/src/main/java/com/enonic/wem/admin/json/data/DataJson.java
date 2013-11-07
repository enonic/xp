package com.enonic.wem.admin.json.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import com.enonic.wem.api.data.Data;
import com.enonic.wem.api.data.RootDataSet;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.WRAPPER_OBJECT)
@JsonSubTypes(
    {@JsonSubTypes.Type(value = PropertyJson.class, name = "Property"), @JsonSubTypes.Type(value = DataSetJson.class, name = "DataSet"),
        @JsonSubTypes.Type(value = RootDataSet.class, name = "RootDataSet")})
public abstract class DataJson<T extends Data>
{
    private T data;

    protected DataJson( final T data )
    {
        this.data = data;
    }

    @JsonIgnore
    public T getData()
    {
        return data;
    }

    public String getName()
    {
        return data.getName();
    }
}
