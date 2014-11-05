module app.browse {

    export class EditPrincipalEvent extends BaseUserEvent {

        static on(handler: (event: EditPrincipalEvent) => void) {
            api.event.Event.bind(api.ClassHelper.getFullName(this), handler);
        }

        static un(handler?: (event: EditPrincipalEvent) => void) {
            api.event.Event.unbind(api.ClassHelper.getFullName(this), handler);
        }
    }
}
