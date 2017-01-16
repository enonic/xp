module api.app.wizard {

    export class MinimizeWizardPanelEvent extends api.event.Event {

        static on(handler: (event: MinimizeWizardPanelEvent) => void) {
            api.event.Event.bind(api.ClassHelper.getFullName(this), handler);
        }

        static un(handler?: (event: MinimizeWizardPanelEvent) => void) {
            api.event.Event.unbind(api.ClassHelper.getFullName(this), handler);
        }
    }
}
