module api.liveedit {

    import Content = api.content.Content;
    import PageComponent = api.content.page.PageComponent;
    import ComponentPath = api.content.page.ComponentPath;

    export class PageComponentView<PAGE_COMPONENT extends PageComponent> extends ItemView {

        private parentRegionView: RegionView;

        private pageComponent: PAGE_COMPONENT;

        constructor(type: ItemType, parentRegionView: RegionView, pageComponent: PAGE_COMPONENT, element?: HTMLElement, dummy?: boolean) {
            super(type, element, dummy);
            this.parentRegionView = parentRegionView;
            this.pageComponent = pageComponent;
        }

        getType(): PageComponentItemType {
            return <PageComponentItemType>super.getType();
        }

        getParentRegionView(): RegionView {
            return this.parentRegionView;
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
            return !this.pageComponent ? false : true;
        }

        getComponentPath(): ComponentPath {

            return this.pageComponent.getPath();
        }

        getName(): string {

            var path = this.getComponentPath();
            return path ? path.getComponentName().toString() : '[No Name]';
        }

        getParentItemView(): RegionView {
            return this.parentRegionView;
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

        duplicate(duplicate: PAGE_COMPONENT): PageComponentView<PAGE_COMPONENT> {
            throw new Error("Must be implemented by inheritors");
        }

        static findParentRegionViewHTMLElement(htmlElement: HTMLElement): HTMLElement {

            var parentItemView = ItemView.findParentItemViewAsHTMLElement(htmlElement);
            while (!RegionView.isRegionViewFromHTMLElement(parentItemView)) {
                parentItemView = ItemView.findParentItemViewAsHTMLElement(parentItemView);
            }
            return parentItemView;
        }

        static findPrecedingComponentItemViewId(htmlElement: HTMLElement): ItemViewId {

            var previousItemView = ItemView.findPreviousItemView(htmlElement);
            if (!previousItemView) {
                return null;
            }

            var asString = previousItemView.getData("live-edit-id");
            if (api.util.isStringEmpty(asString)) {
                return null;
            }
            return ItemViewId.fromString(asString);
        }
    }
}