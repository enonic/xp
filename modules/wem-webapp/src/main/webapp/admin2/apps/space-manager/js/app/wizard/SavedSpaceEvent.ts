module app_wizard {

    export class SavedSpaceEvent extends api_event.Event {

        constructor() {
            super('savedSpace');
        }

        static on(handler:(event:SavedSpaceEvent) => void) {
            api_event.onEvent('savedSpace', handler);
        }
    }

}
