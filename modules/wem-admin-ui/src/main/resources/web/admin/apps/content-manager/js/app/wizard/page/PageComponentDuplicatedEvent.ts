module app.wizard.page {

    import RegionPath = api.content.page.RegionPath;
    import ComponentPath = api.content.page.ComponentPath;
    import PageComponentType = api.content.page.PageComponentType;

    export class PageComponentDuplicatedEvent {

        private componentView: api.dom.Element;

        private type: PageComponentType;

        private region: RegionPath;

        private path: ComponentPath;

        constructor(componentView: api.dom.Element, type: PageComponentType, region: RegionPath, path: ComponentPath) {
            this.componentView = componentView;
            this.type = type;
            this.region = region;
            this.path = path;
        }

        getComponentView() {
            return this.componentView;
        }

        getType(): PageComponentType {
            return this.type;
        }

        getRegion(): RegionPath {
            return this.region;
        }

        getPath(): ComponentPath {
            return this.path;
        }
    }
}