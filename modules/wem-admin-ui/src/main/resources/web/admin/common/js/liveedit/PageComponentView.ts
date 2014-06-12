module api.liveedit {

    import Content = api.content.Content;
    import PageComponent = api.content.page.PageComponent;
    import ComponentPath = api.content.page.ComponentPath;

    export class PageComponentViewBuilder<PAGE_COMPONENT extends PageComponent> {

        itemViewProducer: ItemViewIdProducer;

        type: PageComponentItemType;

        parentRegionView: RegionView;

        pageComponent: PAGE_COMPONENT;

        element: HTMLElement;

        /**
         * Optional. The ItemViewIdProducer of parentRegionView will be used if not set.
         */
        setItemViewProducer(value: ItemViewIdProducer): PageComponentViewBuilder<PAGE_COMPONENT> {
            this.itemViewProducer = value;
            return this;
        }

        setType(value: PageComponentItemType): PageComponentViewBuilder<PAGE_COMPONENT> {
            this.type = value;
            return this;
        }

        setParentRegionView(value: RegionView): PageComponentViewBuilder<PAGE_COMPONENT> {
            this.parentRegionView = value;
            return this;
        }

        setPageComponent(value: PAGE_COMPONENT): PageComponentViewBuilder<PAGE_COMPONENT> {
            this.pageComponent = value;
            return this;
        }

        setElement(value: HTMLElement): PageComponentViewBuilder<PAGE_COMPONENT> {
            this.element = value;
            return this;
        }
    }

    export class PageComponentView<PAGE_COMPONENT extends PageComponent> extends ItemView {

        private parentRegionView: RegionView;

        private pageComponent: PAGE_COMPONENT;

        constructor(builder: PageComponentViewBuilder<PAGE_COMPONENT>) {
            super(new ItemViewBuilder().
                setItemViewIdProducer(builder.itemViewProducer
                    ? builder.itemViewProducer
                    : builder.parentRegionView.getItemViewIdProducer()).
                setType(builder.type).
                setElement(builder.element).
                setParentElement(builder.parentRegionView.getHTMLElement()));
            this.parentRegionView = builder.parentRegionView;

            this.setPageComponent(builder.pageComponent);
        }

        getType(): PageComponentItemType {
            return <PageComponentItemType>super.getType();
        }

        setPageComponent(pageComponent: PAGE_COMPONENT) {
            this.pageComponent = pageComponent;
            if (pageComponent) {
                this.setTooltipObject(pageComponent);
            }
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

        select(clickPosition?: Position) {
            super.select(clickPosition);
            new PageComponentSelectEvent(this).fire();
        }

        handleDragStart() {
            if (this.isSelected()) {
                this.getEl().setData("live-edit-selected-on-sort-start", "true");
            }
        }

        empty() {

            this.getEl().setData('live-edit-empty-component', 'true');
            this.addClass("live-edit-empty-component");
        }

        duplicate(duplicate: PAGE_COMPONENT): PageComponentView<PAGE_COMPONENT> {
            throw new Error("Must be implemented by inheritors");
        }

        addPadding() {
            this.addClass("live-edit-component-padding");
        }

        removePadding() {
            this.removeClass("live-edit-component-padding");
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