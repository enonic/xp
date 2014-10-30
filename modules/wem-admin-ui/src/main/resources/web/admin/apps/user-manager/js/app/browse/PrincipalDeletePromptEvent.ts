module app.browse {

    export class PrincipalDeletePromptEvent extends BasePrincipalEvent {

        static on(handler: (event: PrincipalDeletePromptEvent) => void) {
            api.event.Event.bind(api.ClassHelper.getFullName(this), handler);
        }

        static un(handler?: (event: PrincipalDeletePromptEvent) => void) {
            api.event.Event.unbind(api.ClassHelper.getFullName(this), handler);
        }
    }
}
