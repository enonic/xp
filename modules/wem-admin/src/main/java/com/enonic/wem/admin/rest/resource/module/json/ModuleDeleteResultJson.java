package com.enonic.wem.admin.rest.resource.module.json;

import com.enonic.wem.admin.rest.ResultJson;
import com.enonic.wem.admin.rest.resource.ErrorJson;
import com.enonic.wem.api.module.ModuleKey;

public class ModuleDeleteResultJson extends ResultJson<String> {

    private ModuleDeleteResultJson(String result, ErrorJson error) {
        super(result, error);
    }

    public static ModuleDeleteResultJson error(String error) {
        return new ModuleDeleteResultJson(null, new ErrorJson(error));
    }

    public static ModuleDeleteResultJson result(ModuleKey moduleKey) {
        return new ModuleDeleteResultJson(moduleKey.toString(), null);
    }
}
