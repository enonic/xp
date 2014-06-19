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

    export class OpenPublishDialogEvent extends api.event.Event2 {
        private content: any;

        constructor(content: any) {
            super();
            this.content = content;
        }

        getContent(): any {
            return this.content;
        }

        static on(handler: (event: OpenPublishDialogEvent) => void, contextWindow: Window = window) {
            api.event.Event2.bind(api.util.getFullName(this), handler, contextWindow);
        }

        static un(handler?: (event: OpenPublishDialogEvent) => void, contextWindow: Window = window) {
            api.event.Event2.unbind(api.util.getFullName(this), handler, contextWindow);
        }
    }

    export class PublishContentEvent extends api.event.Event2 {
        private content: any;

        constructor(content: any) {
            super();
            this.content = content;
        }

        getContent(): any {
            return this.content;
        }

        static on(handler: (event: PublishContentEvent) => void, contextWindow: Window = window) {
            api.event.Event2.bind(api.util.getFullName(this), handler, contextWindow);
        }

        static un(handler?: (event: PublishContentEvent) => void, contextWindow: Window = window) {
            api.event.Event2.unbind(api.util.getFullName(this), handler, contextWindow);
        }
    }
}