module api.liveedit {

    import Region = api.content.page.region.Region;
    import RegionPath = api.content.page.RegionPath;
    import PageComponent = api.content.page.PageComponent;

    export class RegionViewBuilder {

        parentView: ItemView;

        region: Region;

        element: HTMLElement;

        setParentView(value: ItemView): RegionViewBuilder {
            this.parentView = value;
            return this;
        }

        setRegion(value: Region): RegionViewBuilder {
            this.region = value;
            return this;
        }

        setElement(value: HTMLElement): RegionViewBuilder {
            this.element = value;
            return this;
        }

    }

    export class RegionView extends ItemView {

        private parentView: ItemView;

        private region: Region;

        private pageComponentViews: PageComponentView<PageComponent>[] = [];

        private placeholder: RegionPlaceholder;

        constructor(builder: RegionViewBuilder) {
            super(new ItemViewBuilder().
                setItemViewIdProducer(builder.parentView.getItemViewIdProducer()).
                setType(RegionItemType.get()).
                setElement(builder.element));
            this.setRegion(builder.region);

            this.parentView = builder.parentView;
            this.placeholder = new RegionPlaceholder(this);
            this.placeholder.hide();
            this.appendChild(this.placeholder);

            this.parsePageComponentViews(this);
        }

        getParentItemView(): ItemView {
            return this.parentView;
        }

        setRegion(region: Region) {
            this.region = region;
            if (region) {
                this.setTooltipObject(region);

                var components = region.getComponents();
                this.getPageComponentViews().forEach((view: PageComponentView<PageComponent>, index: number) => {
                    var pageComponent = components[index];
                    view.setPageComponent(pageComponent);
                });
            }
        }

        getRegion(): Region {
            return this.region;
        }

        getRegionName(): string {
            return this.getRegionPath().getRegionName();
        }

        getRegionPath(): RegionPath {

            return this.region.getPath();
        }

        getName(): string {

            return this.getRegionName().toString();
        }

        select() {
            new RegionSelectEvent(this.getRegionPath(), this).fire();
            super.select();
        }

        getTooltipViewer(): api.ui.Viewer<Region> {
            return new RegionComponentViewer();
        }

        registerPageComponentView(view: PageComponentView<PageComponent>) {
            this.pageComponentViews.push(view);
            this.placeholder.hide();
        }

        addPageComponentView(pageComponentView: PageComponentView<PageComponent>, index: number) {
            this.pageComponentViews.splice(index, 0, pageComponentView);
            this.insertChild(pageComponentView, index);
            this.placeholder.hide();
        }

        getPageComponentViews(): PageComponentView<PageComponent>[] {
            return this.pageComponentViews;
        }

        removePageComponentView(pageComponentView: PageComponentView<PageComponent>) {

            pageComponentView.getElement().remove();

            var indexToRemove = -1;
            this.pageComponentViews.forEach((curr: PageComponentView<PageComponent>, index: number) => {
                if (curr.getItemId().equals(pageComponentView.getItemId())) {
                    console.log("RegionView[" + this.getItemId().toNumber() + "].removePageComponentView: removing PageComponentView: " +
                                curr.getItemId().toString());
                    indexToRemove = index;
                }
            });

            if (indexToRemove >= 0) {
                this.pageComponentViews.splice(indexToRemove, 1);
                if (this.pageComponentViews.length == 0) {
                    console.log("RegionView[" + this.getItemId().toNumber() +
                                "].removePageComponentView: region is now empty, showing placeholder");
                    this.placeholder.show();
                }
            }
        }

        refreshPlaceholder() {
            if (this.pageComponentViews.length == 0) {
                this.placeholder.show();
            }
            else {
                this.placeholder.hide();
            }
        }

        isRegionEmpty(): boolean {

            var hasNotDropTargetPlaceholder: boolean = wemjq(this.getHTMLElement()).children('.live-edit-drop-target-placeholder').length ===
                                                       0;
            return this.pageComponentViews.length == 0 && hasNotDropTargetPlaceholder;
            //var hasNotParts: Boolean = regionElement.children('[data-live-edit-type]' + ':not(:hidden)').length === 0;
            //var hasNotDropTargetPlaceholder: Boolean = regionElement.children('.live-edit-drop-target-placeholder').length === 0;
            //return hasNotParts && hasNotDropTargetPlaceholder;
        }

        toItemViewArray(): ItemView[] {

            var array: ItemView[] = [];
            array.push(this);
            this.pageComponentViews.forEach((pageComponentView: PageComponentView<PageComponent>) => {
                array = array.concat(pageComponentView.toItemViewArray());
            });
            return array;
        }

        static isRegionViewFromHTMLElement(htmlElement: HTMLElement): boolean {

            var type = htmlElement.getAttribute("data-" + ItemType.DATA_ATTRIBUTE);
            if (api.util.isStringBlank(type)) {
                return false;
            }
            return type == "region";
        }

        private parsePageComponentViews(regionView: RegionView) {

            var children = regionView.getChildren();
            var region = regionView.getRegion();
            var pageComponentCount = 0;
            children.forEach((childElement: api.dom.Element) => {
                var itemType = ItemType.fromElement(childElement);
                if (itemType) {
                    api.util.assert(itemType.isPageComponentType(),
                            "Expected ItemView beneath a Region to be a PageComponent: " + itemType.getShortName());

                    var pageComponent = region.getComponentByIndex(pageComponentCount++);
                    var pageComponentView = <PageComponentView<PageComponent>>itemType.createView(
                        new CreateItemViewConfig().
                            setParent(regionView).
                            setData(pageComponent).
                            setElement(childElement.getHTMLElement()));
                    regionView.registerPageComponentView(pageComponentView);
                }
            });
        }
    }
}