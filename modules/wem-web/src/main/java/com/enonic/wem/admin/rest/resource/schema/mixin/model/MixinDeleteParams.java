package com.enonic.wem.admin.rest.resource.schema.mixin.model;

import java.util.List;

public class MixinDeleteParams
{
    private List<String> qualifiedMixinNames;

    public void setQualifiedMixinNames(List<String> names)
    {
        this.qualifiedMixinNames = names;
    }

    public List<String> getQualifiedMixinNames()
    {
        return this.qualifiedMixinNames;
    }
}
