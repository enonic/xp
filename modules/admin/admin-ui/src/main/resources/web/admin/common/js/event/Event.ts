module api.event {

    export class Event {

        private name: string;

        constructor(name?: string) {
            this.name = name || api.ClassHelper.getFullName(this);
        }

        getName(): string {
            return this.name;
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