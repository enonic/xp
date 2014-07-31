module app.browse {

    import ModuleSummary = api.module.ModuleSummary;
    import Event2 = api.event.Event2;

    export class StopModuleEvent extends Event2 {
        private modules: ModuleSummary[];

        constructor(modules: ModuleSummary[]) {
            this.modules = modules;
            super();
        }

        getModules(): ModuleSummary[] {
            return this.modules;
        }

        static on(handler: (event: StopModuleEvent) => void) {
            Event2.bind(api.util.getFullName(this), handler);
        }

        static un(handler?: (event: StopModuleEvent) => void) {
            Event2.unbind(api.util.getFullName(this), handler);
        }
    }
}
