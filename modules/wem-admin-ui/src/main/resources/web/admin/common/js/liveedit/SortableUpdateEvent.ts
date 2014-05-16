module api.liveedit {

    import Event2 = api.event.Event2;
    import ComponentPath = api.content.page.ComponentPath;
    import RegionPath = api.content.page.RegionPath;

    export class SortableUpdateEvent extends Event2 {

        private componentView: any;

        private componentPath: ComponentPath;

        private region: RegionPath;

        private precedingComponent: ComponentPath;

        constructor(componentView: any) {
            super();
            this.componentView = componentView;
            this.componentPath = componentView ? ComponentPath.fromString(componentView.getComponentPath()) : null;
            this.region = componentView ? RegionPath.fromString(componentView.getRegionName()) : null;
            this.precedingComponent = componentView ? ComponentPath.fromString(componentView.getPrecedingComponentPath()) : null;
        }

        getComponentView(): any {
            return this.componentView;
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