module app.wizard {

    export class ToggleContextWindowEvent extends api.event.Event {

        private fixed: boolean;

        constructor(fixed: boolean) {
            super();
            this.fixed = fixed;
        }

        isFixed(): boolean {
            return this.fixed;
        }

        static on(handler: (event: ToggleContextWindowEvent) => void) {
            api.event.Event.bind(api.util.getFullName(this), handler);
        }

        static un(handler?: (event: ToggleContextWindowEvent) => void) {
            api.event.Event.unbind(api.util.getFullName(this), handler);
        }
    }
}