module app.browse {

    import Event2 = api.event.Event2;

    export class InstallModuleEvent extends Event2 {

        static on(handler: (event: InstallModuleEvent) => void) {
            Event2.bind(api.util.getFullName(this), handler);
        }

        static un(handler?: (event: InstallModuleEvent) => void) {
            Event2.unbind(api.util.getFullName(this), handler);
        }
    }
}
