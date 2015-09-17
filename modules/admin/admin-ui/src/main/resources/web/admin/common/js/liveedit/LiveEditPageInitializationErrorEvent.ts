module api.liveedit {

    export class LiveEditPageInitializationErrorEvent extends api.event.Event {

        private message: string;

        constructor(message: string) {
            super();
            this.message = message;
        }

        getMessage(): string {
            return this.message;
        }

        static on(handler: (event: LiveEditPageInitializationErrorEvent) => void, contextWindow: Window = window) {
            api.event.Event.bind(api.ClassHelper.getFullName(this), handler, contextWindow);
        }

        static un(handler?: (event: LiveEditPageInitializationErrorEvent) => void, contextWindow: Window = window) {
            api.event.Event.unbind(api.ClassHelper.getFullName(this), handler, contextWindow);
        }
    }
}