module app.browse {

    import ModuleSummary = api.module.ModuleSummary;

    export class InstallModuleEvent extends api.event.Event {

        constructor() {
            super('installModule');
        }

        static on(handler: (event: InstallModuleEvent) => void) {
            api.event.onEvent('installModule', handler);
        }
    }
}
