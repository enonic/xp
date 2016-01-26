module app.installation {

    export class InstallAppPromptEvent extends api.event.Event {

        static on(handler: (event: InstallAppPromptEvent) => void) {
            api.event.Event.bind(api.ClassHelper.getFullName(this), handler);
        }

        static un(handler?: (event: InstallAppPromptEvent) => void) {
            api.event.Event.unbind(api.ClassHelper.getFullName(this), handler);
        }
    }
}