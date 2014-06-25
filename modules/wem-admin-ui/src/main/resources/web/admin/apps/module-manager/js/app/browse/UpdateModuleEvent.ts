module app.browse {

    import ModuleSummary = api.module.ModuleSummary;

    export class UpdateModuleEvent extends api.event.Event {
        private modules: api.module.ModuleSummary[];

        constructor(modules: ModuleSummary[]) {
            this.modules = modules;
            super('updateModule');
        }

        getModules(): api.module.ModuleSummary[] {
            return this.modules;
        }

        static on(handler: (event: UpdateModuleEvent) => void) {
            api.event.onEvent('updateModule', handler);
        }
    }
}
