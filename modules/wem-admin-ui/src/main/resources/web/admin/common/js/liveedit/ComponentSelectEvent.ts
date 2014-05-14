module api.liveedit {

    import Event2 = api.event.Event2;

    export class ComponentSelectEvent extends Event2 {

        private pathAsString: string;

        private component: any;

        constructor(pathAsString: string, component: any) {
            super();
            this.pathAsString = pathAsString;
            this.component = component;
        }

        getPathAsString(): string {
            return this.pathAsString;
        }

        getComponent(): any {
            return this.component;
        }

        static on(handler: (event: ComponentSelectEvent) => void, contextWindow: Window = window) {
            Event2.bind(api.util.getFullName(this), handler, contextWindow);
        }

        static un(handler?: (event: ComponentSelectEvent) => void, contextWindow: Window = window) {
            Event2.unbind(api.util.getFullName(this), handler, contextWindow);
        }
    }
}