module api.content.site {

    import ModuleKey = api.module.ModuleKey;

    export class ModuleRemovedEvent {

        private moduleKey: ModuleKey;

        constructor(moduleKey: ModuleKey) {

            this.moduleKey = moduleKey;
        }

        getModuleKey(): ModuleKey {
            return this.moduleKey;
        }
    }
}