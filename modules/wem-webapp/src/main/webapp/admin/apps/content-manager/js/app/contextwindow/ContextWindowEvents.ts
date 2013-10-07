module app_contextwindow {
    export class ComponentSelectEvent extends api_event.Event {
        private component:Component;

        constructor(component:Component) {
            this.component = component;
            super('componentSelect');
        }

        getComponent():Component {
            return this.component;
        }

        static on(handler:(event:ComponentSelectEvent) => void) {
            api_event.onEvent('componentSelect', handler);
        }
    }

    export class ComponentDeselectEvent extends api_event.Event {
        constructor() {
            super('componentDeselect');
        }

        static on(handler:(event:ComponentDeselectEvent) => void) {
            api_event.onEvent('componentDeselect', handler);
        }
    }

    export class ComponentRemovedEvent extends api_event.Event {
        constructor() {
            super('componentRemoved');
        }

        static on(handler:(event:ComponentRemovedEvent) => void) {
            api_event.onEvent('componentRemoved', handler);
        }
    }
}