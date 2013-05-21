module APP.event {
    export class OpenSpaceWizardEvent extends API.event.Event {

        constructor() {
            super('openSpaceWizardEvent');
        }

        static on(handler:(event:ShowContextMenuEvent) => void) {
            API.event.onEvent('openSpaceWizardEvent', handler);
        }
    }
}
