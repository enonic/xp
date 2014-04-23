module app.wizard.page {

    import RegionPath = api.content.page.RegionPath;
    import ComponentPath = api.content.page.ComponentPath;

    export class PageComponentDuplicatedEvent {

        private element:api.dom.Element;

        private placeholder:api.dom.Element;

        private type: string;

        private region: RegionPath;

        private path: ComponentPath;

        constructor(element:api.dom.Element, placeholder:api.dom.Element, type: string, region: RegionPath, path: ComponentPath) {
            this.element = element;
            this.placeholder = placeholder;
            this.type = type;
            this.region = region;
            this.path = path;
        }

        getElement(): api.dom.Element {
            return this.element;
        }

        getPlaceholder() {
            return this.placeholder;
        }

        getType(): string {
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