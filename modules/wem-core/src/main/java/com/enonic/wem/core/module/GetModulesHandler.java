package com.enonic.wem.core.module;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import com.enonic.wem.api.command.module.GetModules;
import com.enonic.wem.api.module.Module;
import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.module.ModuleKeys;
import com.enonic.wem.api.module.Modules;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.config.SystemConfig;
import com.enonic.wem.core.exporters.ModuleExporter;

public class GetModulesHandler
        extends CommandHandler<GetModules> {

    private SystemConfig systemConfig;

    private ModuleExporter moduleExporter;

    @Override
    public void handle()
            throws Exception {
        final ModuleKeys moduleKeys = command.getModules();

        List<Module> modules;

        if (moduleKeys != null) {
            modules = getModulesByKeys(moduleKeys);
        } else {
            modules = getAllModules();
        }


        command.setResult(Modules.from(modules));
    }

    private List<Module> getModulesByKeys(ModuleKeys moduleKeys) throws IOException {
        List<Module> modules = new ArrayList<>();
        for (ModuleKey moduleKey : moduleKeys) {
            final File moduleDir = new File(systemConfig.getModulesDir(), moduleKey.toString());
            if (moduleDir.exists() && moduleDir.isDirectory()) {
                Module module = moduleExporter.importFromDirectory(moduleDir.toPath());
                modules.add(module);
            }
        }
        return modules;
    }

    private List<Module> getAllModules() throws IOException {
        List<Module> modules = new ArrayList<>();
        for (File moduleDir: systemConfig.getModulesDir().listFiles()) {
            if (moduleDir.isDirectory()) {
                Module module = moduleExporter.importFromDirectory(moduleDir.toPath());
                modules.add(module);
            }
        }
        return modules;
    }

    @Inject
    public void setSystemConfig(final SystemConfig systemConfig) {
        this.systemConfig = systemConfig;
    }

    @Inject
    public void setModuleExporter(final ModuleExporter moduleExporter) {
        this.moduleExporter = moduleExporter;
    }
}
