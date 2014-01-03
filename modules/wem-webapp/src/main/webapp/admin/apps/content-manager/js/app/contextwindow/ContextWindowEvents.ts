module app.contextwindow {
    export class ComponentSelectEvent extends api.event.Event {
        private component:Component;

        constructor(component:Component) {
            this.component = component;
            super('componentSelect');
        }

        getComponent():Component {
            return this.component;
        }

        static on(handler:(event:ComponentSelectEvent) => void) {
            api.event.onEvent('componentSelect', handler);
        }
    }

    export class ComponentDeselectEvent extends api.event.Event {
        constructor() {
            super('componentDeselect');
        }

        static on(handler:(event:ComponentDeselectEvent) => void) {
            api.event.onEvent('componentDeselect', handler);
        }
    }

    export class ComponentRemovedEvent extends api.event.Event {
        constructor() {
            super('componentRemoved');
        }

        static on(handler:(event:ComponentRemovedEvent) => void) {
            api.event.onEvent('componentRemoved', handler);
        }
    }

    export class LiveEditDragStartEvent extends api.event.Event {
        constructor() {
            super('liveEditDragStart');
        }

        static on(handler:(event:LiveEditDragStartEvent) => void) {
            api.event.onEvent('liveEditDragStart', handler);
        }
    }

    export class LiveEditDragStopEvent extends api.event.Event {
        constructor() {
            super('liveEditDragStop');
        }

        static on(handler:(event:LiveEditDragStopEvent) => void) {
            api.event.onEvent('liveEditDragStop', handler);
        }
    }
}