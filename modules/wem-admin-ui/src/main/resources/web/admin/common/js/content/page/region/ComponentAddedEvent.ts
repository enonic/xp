module api.content.page.region {

    export class ComponentAddedEvent extends RegionChangedEvent {

        private componentPath: ComponentPath;

        constructor(regionPath: RegionPath, componentPath: ComponentPath) {
            super(regionPath);
            this.componentPath = componentPath;
        }

        getComponentPath(): ComponentPath {
            return this.componentPath;
        }
    }
}