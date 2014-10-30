module app.browse {

    export class ShowNewPrincipalDialogEvent extends BasePrincipalEvent {

        static on(handler: (event: ShowNewPrincipalDialogEvent) => void) {
            api.event.Event.bind(api.ClassHelper.getFullName(this), handler);
        }

        static un(handler?: (event: ShowNewPrincipalDialogEvent) => void) {
            api.event.Event.unbind(api.ClassHelper.getFullName(this), handler);
        }
    }
}
