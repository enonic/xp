package com.enonic.wem.admin.rest.resource.module.json;

import com.enonic.wem.api.module.ModuleKey;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ModuleDeleteParams {

    private ModuleKey moduleKey;

    @JsonCreator
    public ModuleDeleteParams(@JsonProperty("key") String key) {
        this.moduleKey = ModuleKey.from(key);
    }

    public ModuleKey getModuleKey() {
        return moduleKey;
    }
}
