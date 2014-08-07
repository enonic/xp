module app.wizard {

    export class ToggleContextWindowEvent extends api.event.Event {

        private toggler: ContextWindowToggler;

        constructor(toggler: ContextWindowToggler) {
            super();
            this.toggler = toggler;
        }

        getToggler(): ContextWindowToggler {
            return this.toggler;
        }

        static on(handler: (event: ToggleContextWindowEvent) => void) {
            api.event.Event.bind(api.util.getFullName(this), handler);
        }

        static un(handler?: (event: ToggleContextWindowEvent) => void) {
            api.event.Event.unbind(api.util.getFullName(this), handler);
        }
    }
}