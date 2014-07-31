module app.wizard {

    export class ToggleContextWindowEvent extends api.event.Event2 {

        private fixed: boolean;

        constructor(fixed: boolean) {
            super();
            this.fixed = fixed;
        }

        isFixed(): boolean {
            return this.fixed;
        }

        static on(handler: (event: ToggleContextWindowEvent) => void) {
            api.event.Event2.bind(api.util.getFullName(this), handler);
        }

        static un(handler?: (event: ToggleContextWindowEvent) => void) {
            api.event.Event2.unbind(api.util.getFullName(this), handler);
        }
    }
}