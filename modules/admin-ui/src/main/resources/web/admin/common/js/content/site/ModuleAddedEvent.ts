module api.content.site {
    export class ModuleAddedEvent {

        private moduleConfig: ModuleConfig;

        constructor(moduleConfig: ModuleConfig) {

            this.moduleConfig = moduleConfig;
        }

        getModuleConfig(): ModuleConfig {
            return this.moduleConfig;
        }
    }
}