package com.enonic.wem.admin.json.module;

import com.enonic.wem.api.module.Module;
import com.enonic.wem.api.module.Modules;
import com.google.common.collect.ImmutableList;

import java.util.List;

public class ListModuleJson {

    private List<ModuleSummaryJson> list;

    public ListModuleJson(Modules modules) {
        ImmutableList.Builder<ModuleSummaryJson> builder = ImmutableList.builder();
        for (Module module : modules) {
            builder.add(new ModuleSummaryJson(module));
        }
        this.list = builder.build();
    }

    public int getTotal() {
        return this.list.size();
    }

    public List<ModuleSummaryJson> getModules() {
        return this.list;
    }
}
