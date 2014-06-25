module app.browse {

    import ModuleSummary = api.module.ModuleSummary;

    export class StartModuleEvent extends api.event.Event {
        private modules: api.module.ModuleSummary[];

        constructor(modules: ModuleSummary[]) {
            this.modules = modules;
            super('startModule');
        }

        getModules(): api.module.ModuleSummary[] {
            return this.modules;
        }

        static on(handler: (event: StartModuleEvent) => void) {
            api.event.onEvent('startModule', handler);
        }
    }
}
