module app.browse {

    export class RefreshModulesEvent extends api.event.Event {

        constructor() {
            super('refreshModules');
        }

        static on(handler: (event: RefreshModulesEvent) => void) {
            api.event.onEvent('refreshModules', handler);
        }
    }
}
