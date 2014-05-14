module app.wizard.page {

    import ComponentPath = api.content.page.ComponentPath;
    import RegionPath = api.content.page.RegionPath;

    export class SortableUpdateEvent {

        private componentPath: ComponentPath;

        private componentView: any;

        private region: RegionPath;

        private precedingComponent: ComponentPath;

        constructor(componentPath: ComponentPath, componentView: any, region: RegionPath, precedingComponent: ComponentPath) {
            this.componentPath = componentPath;
            this.componentView = componentView;
            this.region = region;
            this.precedingComponent = precedingComponent;
        }

        getComponentPath(): ComponentPath {
            return this.componentPath;
        }

        getComponentView(): any {
            return this.componentView;
        }

        getRegion(): RegionPath {
            return this.region;
        }

        getPrecedingComponent(): ComponentPath {
            return this.precedingComponent;
        }
    }
}