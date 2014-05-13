module app.wizard.page {

    import ComponentPath = api.content.page.ComponentPath;
    import PageComponentType = api.content.page.PageComponentType;
    import RegionPath = api.content.page.RegionPath;

    export class PageComponentAddedEvent {

        private element: api.dom.Element;

        private type: PageComponentType;

        private region: RegionPath;

        private precedingComponent: ComponentPath;

        constructor(element: api.dom.Element, type: PageComponentType, region: RegionPath, precedingComponent: ComponentPath) {
            this.element = element;
            this.type = type;
            this.region = region;
            this.precedingComponent = precedingComponent;
        }

        getElement(): api.dom.Element {
            return this.element;
        }

        getType(): PageComponentType {
            return this.type;
        }

        getRegion(): RegionPath {
            return this.region;
        }

        getPrecedingComponent(): ComponentPath {
            return this.precedingComponent;
        }
    }
}