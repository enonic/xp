module app_wizard_event {

    export class ShowContentFormEvent extends api_event.Event {

        constructor() {
            super('showContentForm');
        }

        static on(handler:(event:ShowContentFormEvent) => void) {
            api_event.onEvent('showContentForm', handler);
        }

    }

}