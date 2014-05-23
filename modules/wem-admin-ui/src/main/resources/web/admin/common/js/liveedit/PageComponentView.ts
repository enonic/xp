module api.liveedit {

    import Content = api.content.Content;
    import ComponentPath = api.content.page.ComponentPath;

    export class PageComponentView extends ItemView {

        constructor(type: ItemType, element?: HTMLElement, dummy?: boolean) {
            super(type, element, dummy);
        }

        setComponentPath(path: ComponentPath) {
            this.getEl().setData('live-edit-component', path.toString());
        }

        hasComponentPath(): boolean {
            return this.getEl().hasAttribute('data-live-edit-component');
        }

        getComponentPath(): ComponentPath {
            var asString = this.getEl().getData('live-edit-component');
            return api.content.page.ComponentPath.fromString(asString);
        }

        getName(): string {

            var path = this.getComponentPath();
            return path ? path.getComponentName().toString() : '[No Name]';
        }

        getParentRegion(): RegionView {
            api.util.assert(this.getType().isPageComponentType(),
                    "Expected to only be called when this is a PageComponent: " + api.util.getClassName(this));

            var parentElement = this.getHTMLElement().parentElement;
            if (!parentElement) {
                return null;
            }
            var type = ItemType.fromHTMLElement(parentElement);
            if (!type || !type.equals(RegionItemType.get())) {
                return null;
            }

            return RegionView.fromHTMLElement(parentElement);
        }

        getPrecedingComponentPath(): ComponentPath {
            api.util.assert(this.getType().isPageComponentType(),
                    "Expected to only be called when this is a PageComponent: " + api.util.getClassName(this));

            var previousElement = this.getPreviousElement();
            if (!previousElement) {
                return null;
            }

            var asString = previousElement.getEl().getData('live-edit-component');
            return api.content.page.ComponentPath.fromString(asString);
        }

        select() {
            new PageComponentSelectEvent(this.getComponentPath(), this).fire();
            super.select();
        }

        public static fromHTMLElement(element: HTMLElement, dummy: boolean = true): PageComponentView {

            var type = ItemType.fromHTMLElement(element);
            api.util.assert(type.isPageComponentType(), "Expected ItemType to be PageComponent");

            return <PageComponentView>type.createView(element, dummy);
        }

        public static fromJQuery(element: JQuery, dummy: boolean = true): PageComponentView {
            return PageComponentView.fromHTMLElement(element.get(0), dummy)
        }
    }
}