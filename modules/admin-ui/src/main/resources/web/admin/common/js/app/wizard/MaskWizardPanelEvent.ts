module api.app.wizard {

    export class MaskWizardPanelEvent extends api.event.Event {

        private mask: boolean;

        constructor(mask: boolean = true) {
            super();

            this.mask = mask;
        }

        isMask(): boolean {
            return this.mask;
        }

        static on(handler: (event: MaskWizardPanelEvent) => void) {
            api.event.Event.bind(api.ClassHelper.getFullName(this), handler);
        }

        static un(handler?: (event: MaskWizardPanelEvent) => void) {
            api.event.Event.unbind(api.ClassHelper.getFullName(this), handler);
        }
    }

}