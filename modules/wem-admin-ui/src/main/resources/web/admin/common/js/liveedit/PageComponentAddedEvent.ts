module api.liveedit {

    import Event2 = api.event.Event2;
    import ComponentPath = api.content.page.ComponentPath;
    import PageComponentType = api.content.page.PageComponentType;
    import RegionPath = api.content.page.RegionPath;

    export class PageComponentAddedEvent extends Event2 {

        private component: api.dom.Element;

        private type: PageComponentType;

        private region: RegionPath;

        private precedingComponent: ComponentPath;

        setComponent(component: api.dom.Element): PageComponentAddedEvent {
            this.component = component;
            return this;
        }

        setType(name: string): PageComponentAddedEvent {
            this.type = PageComponentType.byShortName(name);
            return this;
        }

        setRegion(region: string): PageComponentAddedEvent {
            this.region = RegionPath.fromString(region);
            return this;
        }

        setPrecedingComponent(precedingComponent: ComponentPath): PageComponentAddedEvent {
            this.precedingComponent = precedingComponent;
            return this;
        }

        getComponent(): api.dom.Element {
            return this.component;
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

        static on(handler: (event: PageComponentAddedEvent) => void, contextWindow: Window = window) {
            Event2.bind(api.util.getFullName(this), handler, contextWindow);
        }

        static un(handler?: (event: PageComponentAddedEvent) => void, contextWindow: Window = window) {
            Event2.unbind(api.util.getFullName(this), handler, contextWindow);
        }
    }
}