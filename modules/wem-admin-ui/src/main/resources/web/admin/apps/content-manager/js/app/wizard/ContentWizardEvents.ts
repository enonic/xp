module app.wizard {
    export class ToggleContextWindowEvent extends api.event.Event {
        constructor() {
            super('toggleContextWindow');
        }

        static on(handler:(event:ToggleContextWindowEvent) => void) {
            api.event.onEvent('toggleContextWindow', handler);
        }
    }

    export class ShowLiveEditEvent extends api.event.Event {
        constructor() {
            super('showLiveEdit');
        }

        static on(handler:(event:ShowLiveEditEvent) => void) {
            api.event.onEvent('showLiveEdit', handler);
        }
    }

    export class ShowSplitEditEvent extends api.event.Event {
        constructor() {
            super('showSplitEdit');
        }

        static on(handler:(event:ShowSplitEditEvent) => void) {
            api.event.onEvent('showSplitEdit', handler);
        }
    }

    export class ShowContentFormEvent extends api.event.Event {
        constructor() {
            super('showContentForm');
        }

        static on(handler:(event:ShowContentFormEvent) => void) {
            api.event.onEvent('showContentForm', handler);
        }
    }
}