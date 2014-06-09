module api.liveedit {

    import Content = api.content.Content;
    import PageComponent = api.content.page.PageComponent;
    import ComponentPath = api.content.page.ComponentPath;

    export class PageComponentView<PAGE_COMPONENT extends PageComponent> extends ItemView {

        private parentRegionView: RegionView;

        private pageComponent: PAGE_COMPONENT;

        private tooltip: api.ui.Tooltip;

        private tooltipViewer: api.ui.Viewer<PAGE_COMPONENT>;

        constructor(type: ItemType, parentRegionView: RegionView, pageComponent: PAGE_COMPONENT, element?: HTMLElement, dummy?: boolean) {
            super(type, element, dummy, parentRegionView.getHTMLElement());
            this.parentRegionView = parentRegionView;
            this.tooltipViewer = this.getTooltipViewer();
            this.tooltip = new api.ui.Tooltip(this).
                setHideTimeout(0).
                setSide(api.ui.Tooltip.SIDE_TOP).
                setContent(this.tooltipViewer);

            this.setPageComponent(pageComponent);
        }

        getType(): PageComponentItemType {
            return <PageComponentItemType>super.getType();
        }

        getParentRegionView(): RegionView {
            return this.parentRegionView;
        }

        setPageComponent(pageComponent: PAGE_COMPONENT) {
            this.pageComponent = pageComponent;
            if (pageComponent) {
                this.tooltipViewer.setObject(pageComponent);
            }
        }

        getTooltipViewer(): api.ui.Viewer<PAGE_COMPONENT> {
            throw new Error('Should be implemented by inheritors');
        }

        getPageComponent(): PAGE_COMPONENT {
            return this.pageComponent;
        }

        hasComponentPath(): boolean {
            return !this.pageComponent ? false : true;
        }

        getComponentPath(): ComponentPath {

            if (!this.pageComponent) {
                return null;
            }
            return this.pageComponent.getPath();
        }

        getName(): string {

            var path = this.getComponentPath();
            return path ? path.getComponentName().toString() : '[No Name]';
        }

        getParentItemView(): RegionView {
            return this.parentRegionView;
        }

        select(event?: JQueryEventObject) {
            super.select(event);
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

            var asString = previousItemView.getData(ItemViewId.DATA_ATTRIBUTE);
            if (api.util.isStringEmpty(asString)) {
                return null;
            }
            return ItemViewId.fromString(asString);
        }
    }
}