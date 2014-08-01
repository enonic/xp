module app.browse {

    import ModuleSummary = api.module.ModuleSummary;
    import Event = api.event.Event;

    export class StartModuleEvent extends Event {
        private modules: ModuleSummary[];

        constructor(modules: ModuleSummary[]) {
            this.modules = modules;
            super();
        }

        getModules(): ModuleSummary[] {
            return this.modules;
        }

        static on(handler: (event: StartModuleEvent) => void) {
            Event.bind(api.util.getFullName(this), handler);
        }

        static un(handler?: (event: StartModuleEvent) => void) {
            Event.unbind(api.util.getFullName(this), handler);
        }
    }
}
