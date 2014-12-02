module app.browse {

    export class NewPrincipalEvent extends BaseUserEvent {

        static on(handler: (event: NewPrincipalEvent) => void) {
            api.event.Event.bind(api.ClassHelper.getFullName(this), handler);
        }

        static un(handler?: (event: NewPrincipalEvent) => void) {
            api.event.Event.unbind(api.ClassHelper.getFullName(this), handler);
        }
    }
}
