module app_event {

    export class CloseActiveSpacePanelEvent extends api_event.Event {
        constructor() {
            super('closeActiveSpacePanel');
        }

        static on(handler:(event:CloseActiveSpacePanelEvent) => void) {
            api_event.onEvent('closeActiveSpacePanel', handler);
        }
    }
}
