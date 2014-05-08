module api.event {

    export class Event2 {

        private name: string;

        constructor(name: string) {
            this.name = name;
        }

        getName(): string {
            return this.name;
        }

        fire(contextWindow: Window = window) {
            api.event.fireEvent2(this, contextWindow);
        }
    }

}