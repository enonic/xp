module app.wizard.page {

    import ComponentPath = api.content.page.ComponentPath;
    import RegionPath = api.content.page.RegionPath;

    export class SortableUpdateEvent {

        private componentPath: ComponentPath;

        private component: any;

        private region: RegionPath;

        private precedingComponent: ComponentPath;

        constructor(componentPath: ComponentPath, component:any, region: RegionPath, precedingComponent: ComponentPath) {
            this.componentPath = componentPath;
            this.component = component;
            this.region = region;
            this.precedingComponent = precedingComponent;
        }

        getComponentPath(): ComponentPath {
            return this.componentPath;
        }

        getComponent(): any {
            return this.component;
        }

        getRegion(): RegionPath {
            return this.region;
        }

        getPrecedingComponent(): ComponentPath {
            return this.precedingComponent;
        }
    }
}