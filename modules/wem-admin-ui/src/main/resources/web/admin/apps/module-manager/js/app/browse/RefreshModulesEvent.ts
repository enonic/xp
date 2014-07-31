module app.browse {

    import Event2 = api.event.Event2;

    export class RefreshModulesEvent extends Event2 {

        static on(handler: (event: RefreshModulesEvent) => void) {
            Event2.bind(api.util.getFullName(this), handler);
        }

        static un(handler?: (event: RefreshModulesEvent) => void) {
            Event2.unbind(api.util.getFullName(this), handler);
        }
    }
}
