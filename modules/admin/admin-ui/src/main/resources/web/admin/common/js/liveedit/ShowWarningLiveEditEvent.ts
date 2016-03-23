module api.liveedit {

    export class ShowWarningLiveEditEvent extends api.event.Event {

        private message: string;

        constructor(message: string) {
            this.message = message;
            super();
        }

        getMessage(): string {
            return this.message;
        }

        static on(handler: (event: ShowWarningLiveEditEvent) => void, contextWindow: Window = window) {
            api.event.Event.bind(api.ClassHelper.getFullName(this), handler, contextWindow);
        }

        static un(handler?: (event: ShowWarningLiveEditEvent) => void, contextWindow: Window = window) {
            api.event.Event.unbind(api.ClassHelper.getFullName(this), handler, contextWindow);
        }

    }
}