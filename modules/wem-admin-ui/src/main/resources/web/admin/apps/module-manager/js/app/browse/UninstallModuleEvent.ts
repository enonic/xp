module app.browse {

    import ModuleSummary = api.module.ModuleSummary;

    export class UninstallModuleEvent extends api.event.Event {
        private modules: api.module.ModuleSummary[];

        constructor(modules: ModuleSummary[]) {
            this.modules = modules;
            super('uninstallModule');
        }

        getModules(): api.module.ModuleSummary[] {
            return this.modules;
        }

        static on(handler: (event: UninstallModuleEvent) => void) {
            api.event.onEvent('uninstallModule', handler);
        }
    }
}
