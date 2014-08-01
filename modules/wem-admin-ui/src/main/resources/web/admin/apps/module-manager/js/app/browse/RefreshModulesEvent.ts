module app.browse {

    import Event = api.event.Event;

    export class RefreshModulesEvent extends Event {

        static on(handler: (event: RefreshModulesEvent) => void) {
            Event.bind(api.util.getFullName(this), handler);
        }

        static un(handler?: (event: RefreshModulesEvent) => void) {
            Event.unbind(api.util.getFullName(this), handler);
        }
    }
}
