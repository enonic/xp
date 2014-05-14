module api.event {

    export class Event2 {

        private name: string;

        constructor(name?: string) {
            this.name = name || api.util.getFullName(this);
        }

        getName(): string {
            return this.name;
        }

        fire(contextWindow: Window = window) {
            EventBus2.fireEvent(this, contextWindow);
        }

        static bind(name: string, handler: (event: Event2) => void, contextWindow: Window = window) {
            EventBus2.onEvent(name, handler, contextWindow);
        }

        static unbind(name: string, handler?: (event: Event2) => void, contextWindow: Window = window) {
            EventBus2.unEvent2(name, handler, contextWindow);
        }
    }

}