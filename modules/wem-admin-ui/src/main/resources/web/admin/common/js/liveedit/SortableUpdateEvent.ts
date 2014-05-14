module api.liveedit {

    import Event2 = api.event.Event2;
    import ComponentPath = api.content.page.ComponentPath;
    import RegionPath = api.content.page.RegionPath;

    export class SortableUpdateEvent extends Event2 {

        private component: any;

        private componentPath: ComponentPath;

        private region: RegionPath;

        private precedingComponent: ComponentPath;

        constructor(component: any) {
            super();
            this.component = component;
            this.componentPath = component ? ComponentPath.fromString(component.getComponentPath()) : null;
            this.region = component ? RegionPath.fromString(component.getRegionName()) : null;
            this.precedingComponent = component ? ComponentPath.fromString(component.getPrecedingComponentPath()) : null;
        }

        getComponent(): any {
            return this.component;
        }

        getComponentPath(): ComponentPath {
            return this.componentPath;
        }

        getRegion(): RegionPath {
            return this.region;
        }

        getPrecedingComponent(): ComponentPath {
            return this.precedingComponent;
        }

        static on(handler: (event: SortableUpdateEvent) => void, contextWindow: Window = window) {
            Event2.bind(api.util.getFullName(this), handler, contextWindow);
        }

        static un(handler: (event: SortableUpdateEvent) => void, contextWindow: Window = window) {
            Event2.unbind(api.util.getFullName(this), handler, contextWindow);
        }
    }
}