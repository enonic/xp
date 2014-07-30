module app.wizard {

    export class ToggleContextWindowEvent extends api.event.Event {

        private fixed: boolean;

        constructor(fixed: boolean) {
            super('toggleContextWindow');
            this.fixed = fixed;
        }

        isFixed(): boolean {
            return this.fixed;
        }

        static on(handler:(event:ToggleContextWindowEvent) => void) {
            api.event.onEvent('toggleContextWindow', handler);
        }
    }
}