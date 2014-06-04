module api.liveedit {

    import Content = api.content.Content;
    import PageComponent = api.content.page.PageComponent;
    import ComponentPath = api.content.page.ComponentPath;

    export class PageComponentView<PAGE_COMPONENT extends PageComponent> extends ItemView {

        private pageComponent: PAGE_COMPONENT;

        constructor(type: ItemType, element?: HTMLElement, dummy?: boolean) {
            super(type, element, dummy);
        }

        setPageComponent(data: PAGE_COMPONENT) {
            this.pageComponent = data;
        }

        getPageComponent(): PAGE_COMPONENT {
            return this.pageComponent;
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
            super.select();
            new PageComponentSelectEvent(this).fire();
        }

        handleDragStart() {
            if (this.isSelected()) {
                this.getEl().setData("live-edit-selected-on-sort-start", "true");
            }
        }

        handleDragStop() {

        }

        empty() {

            this.getEl().setData('live-edit-empty-component', 'true');
            this.addClass("live-edit-empty-component");
        }

        duplicate(): PageComponentView<PAGE_COMPONENT> {
            throw new Error("Must be implemented by inheritors");
        }

        static findParentRegionViewHTMLElement(htmlElement: HTMLElement): HTMLElement {

            var parentItemView = ItemView.findParentItemViewAsHTMLElement(htmlElement);
            while (!RegionView.isRegionViewFromHTMLElement(parentItemView)) {
                parentItemView = ItemView.findParentItemViewAsHTMLElement(parentItemView);
            }
            return parentItemView;
        }
    }
}