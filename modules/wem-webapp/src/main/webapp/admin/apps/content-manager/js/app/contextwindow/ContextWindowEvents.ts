module app.contextwindow {
    export class SelectComponentEvent extends api.event.Event {
        private component:Component;

        constructor(component:Component) {
            this.component = component;
            super('selectComponent');
        }

        getComponent():Component {
            return this.component;
        }

        static on(handler:(event:SelectComponentEvent) => void) {
            api.event.onEvent('selectComponent', handler);
        }
    }

    export class ComponentSelectEvent extends api.event.Event {
        private componentPath:string;

        constructor(name:string) {
            this.componentPath = name;
            super('componentSelect');
        }

        getComponentPath():string {
            return this.componentPath;
        }

        static on(handler:(event:ComponentSelectEvent) => void) {
            api.event.onEvent('componentSelect', handler);
        }
    }

    export class RegionSelectEvent extends api.event.Event {
        private regionName:string;

        constructor(name:string) {
            this.regionName = name;
            super('regionSelect');
        }

        getName():string {
            return this.regionName;
        }

        static on(handler:(event:RegionSelectEvent) => void) {
            api.event.onEvent('regionSelect', handler);
        }
    }

    export class PageSelectEvent extends api.event.Event {

        constructor() {
            super('pageSelect');
        }

        static on(handler:(event:PageSelectEvent) => void) {
            api.event.onEvent('pageSelect', handler);
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