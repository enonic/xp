module app.wizard.page {

    import ComponentPath = api.content.page.ComponentPath;
    import RegionPath = api.content.page.RegionPath;

    export class PageComponentAddedEvent {

        private element:api.dom.Element;

        private type: string;

        private region: RegionPath;

        private precedingComponent: ComponentPath;

        constructor(element:api.dom.Element, type: string, region: RegionPath, precedingComponent: ComponentPath) {
            this.element = element;
            this.type = type;
            this.region = region;
            this.precedingComponent = precedingComponent;
        }

        getElement(): api.dom.Element {
            return this.element;
        }

        getType(): string {
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