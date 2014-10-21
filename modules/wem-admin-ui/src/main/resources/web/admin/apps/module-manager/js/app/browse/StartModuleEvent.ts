module app.browse {

    import Module = api.module.Module;
    import Event = api.event.Event;

    export class StartModuleEvent extends Event {
        private modules: Module[];

        constructor(modules: Module[]) {
            this.modules = modules;
            super();
        }

        getModules(): Module[] {
            return this.modules;
        }

        static on(handler: (event: StartModuleEvent) => void) {
            Event.bind(api.ClassHelper.getFullName(this), handler);
        }

        static un(handler?: (event: StartModuleEvent) => void) {
            Event.unbind(api.ClassHelper.getFullName(this), handler);
        }
    }
}
