module app.browse {

    import ModuleSummary = api.module.ModuleSummary;
    import Event = api.event.Event;

    export class UninstallModuleEvent extends Event {
        private modules: ModuleSummary[];

        constructor(modules: ModuleSummary[]) {
            this.modules = modules;
            super();
        }

        getModules(): ModuleSummary[] {
            return this.modules;
        }

        static on(handler: (event: UninstallModuleEvent) => void) {
            Event.bind(api.util.getFullName(this), handler);
        }

        static un(handler?: (event: UninstallModuleEvent) => void) {
            Event.unbind(api.util.getFullName(this), handler);
        }
    }
}
