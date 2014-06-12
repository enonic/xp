module api.liveedit.layout {

    import PageComponent = api.content.page.PageComponent;
    import Region = api.content.page.region.Region;
    import LayoutComponent = api.content.page.layout.LayoutComponent;
    import PageComponentView = api.liveedit.PageComponentView;
    import RegionView = api.liveedit.RegionView;
    import ItemView = api.liveedit.ItemView;

    export class LayoutViewBuilder extends PageComponentViewBuilder<LayoutComponent> {

        constructor() {
            super();
            this.setType(LayoutItemType.get());
        }
    }

    export class LayoutView extends PageComponentView<LayoutComponent> {

        private placeholder: LayoutPlaceholder;

        private regionViews: RegionView[];

        constructor(builder: LayoutViewBuilder) {
            this.regionViews = [];
            super(builder);
            this.placeholder = new LayoutPlaceholder(this);
            this.parseRegions();
        }

        addRegion(view: RegionView) {
            this.regionViews.push(view);
        }

        setPageComponent(layoutComponent: LayoutComponent) {
            super.setPageComponent(layoutComponent);
            var regions = layoutComponent.getLayoutRegions().getRegions();
            this.regionViews.forEach((regionView: RegionView, index: number) => {
                var region = regions[index];
                regionView.setRegion(region);
            });
        }

        getRegions(): RegionView[] {
            return this.regionViews;
        }

        select() {
            super.select();
            if (this.isEmpty()) {
                this.placeholder.select();
            }
        }

        deselect() {
            super.deselect();
            if (this.isEmpty()) {
                this.placeholder.deselect();
            }
        }

        empty() {
            super.empty();

            this.removeChildren();
            this.appendChild(this.placeholder);

        }

        duplicate(duplicate: LayoutComponent): LayoutView {

            var duplicatedView = new LayoutView(new LayoutViewBuilder().
                setParentRegionView(this.getParentItemView()).
                setPageComponent(duplicate));
            this.getEl().insertAfterThisEl(duplicatedView.getEl());
            return duplicatedView;
        }

        getTooltipViewer(): LayoutComponentViewer {
            return new LayoutComponentViewer();
        }

        toItemViewArray(): ItemView[] {

            var array: ItemView[] = [];
            array.push(this);
            this.regionViews.forEach((regionView: RegionView) => {
                array = array.concat(regionView.toItemViewArray());
            });
            return array;
        }

        static getClosestParentLayoutView(itemView: ItemView): LayoutView {

            var parent = itemView.getParentItemView();
            while (!api.ObjectHelper.iFrameSafeInstanceOf(parent, LayoutView)) {
                parent = parent.getParentItemView();
                if (parent == null) {
                    break;
                }
            }
            if (!parent) {
                return null;
            }
            return <LayoutView>parent;
        }

        static getParentLayoutView(pageComponentView: PageComponentView<PageComponent>): LayoutView {

            var parentRegion = pageComponentView.getParentItemView();
            var potentialLayoutView = parentRegion.getParentItemView()
            if (api.ObjectHelper.iFrameSafeInstanceOf(potentialLayoutView, LayoutView)) {
                return <LayoutView> potentialLayoutView;
            }
            else {
                return null;
            }
        }

        static hasParentLayoutView(pageComponentView: PageComponentView<PageComponent>): boolean {
            return !LayoutView.getParentLayoutView(pageComponentView) ? false : true;
        }

        private parseRegions() {

            return this.doParseRegions();
        }

        private doParseRegions(parentElement?: api.dom.Element) {

            var layoutComponent = this.getPageComponent();
            var regions: Region[] = layoutComponent.getLayoutRegions().getRegions();
            var children = parentElement ? parentElement.getChildren() : this.getChildren();
            var regionIndex = 0;
            children.forEach((childElement: api.dom.Element) => {
                var itemType = ItemType.fromElement(childElement);
                if (itemType) {
                    if (RegionItemType.get().equals(itemType)) {
                        var region = regions[regionIndex++];
                        var regionView = new RegionView(new RegionViewBuilder().
                            setParentView(this).
                            setRegion(region).
                            setElement(childElement.getHTMLElement()));
                        this.addRegion(regionView);
                    }
                    else {
                        this.doParseRegions(childElement);
                    }
                }
            });
        }
    }
}