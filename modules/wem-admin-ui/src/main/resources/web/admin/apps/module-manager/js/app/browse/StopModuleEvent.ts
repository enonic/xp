module app.browse {

    import ModuleSummary = api.module.ModuleSummary;

    export class StopModuleEvent extends api.event.Event {
        private modules: api.module.ModuleSummary[];

        constructor(modules: ModuleSummary[]) {
            this.modules = modules;
            super('stopModule');
        }

        getModules(): api.module.ModuleSummary[] {
            return this.modules;
        }

        static on(handler: (event: StopModuleEvent) => void) {
            api.event.onEvent('stopModule', handler);
        }
    }
}
