module api.event {

    export class Event {

        private eventName: string;

        constructor(name?: string) {
            this.eventName = name || api.util.getFullName(this);
        }

        getName(): string {
            return this.eventName;
        }

        fire(contextWindow: Window = window) {
            EventBus.fireEvent(this, contextWindow);
        }

        static bind(name: string, handler: (event: Event) => void, contextWindow: Window = window) {
            EventBus.onEvent(name, handler, contextWindow);
        }

        static unbind(name: string, handler?: (event: Event) => void, contextWindow: Window = window) {
            EventBus.unEvent(name, handler, contextWindow);
        }
    }

}