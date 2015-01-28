module api.content.site {
    export class ModuleAddedEvent {

        private moduleConfig: ModuleConfig;

        constructor(moduleConfig: ModuleConfig) {
            this.moduleConfig = moduleConfig;
        }
        getModuleKey() : api.module.ModuleKey {
            return this.moduleConfig.getModuleKey();
        }

        getModuleConfig(): ModuleConfig {
            return this.moduleConfig;
        }
    }
}