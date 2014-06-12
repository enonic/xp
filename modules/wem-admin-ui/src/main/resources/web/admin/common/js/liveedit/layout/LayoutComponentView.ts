module api.liveedit.layout {

    import PageComponent = api.content.page.PageComponent;
    import Region = api.content.page.region.Region;
    import LayoutComponent = api.content.page.layout.LayoutComponent;
    import PageComponentView = api.liveedit.PageComponentView;
    import RegionView = api.liveedit.RegionView;
    import ItemView = api.liveedit.ItemView;

    export class LayoutComponentViewBuilder extends PageComponentViewBuilder<LayoutComponent> {

        constructor() {
            super();
            this.setType(LayoutItemType.get());
        }
    }

    export class LayoutComponentView extends PageComponentView<LayoutComponent> {

        private placeholder: LayoutPlaceholder;

        private regionViews: RegionView[];

        constructor(builder: LayoutComponentViewBuilder) {
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

        duplicate(duplicate: LayoutComponent): LayoutComponentView {

            var duplicatedView = new LayoutComponentView(new LayoutComponentViewBuilder().
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

        static getClosestParentLayoutComponentView(itemView: ItemView): LayoutComponentView {

            var parent = itemView.getParentItemView();
            while (!api.ObjectHelper.iFrameSafeInstanceOf(parent, LayoutComponentView)) {
                parent = parent.getParentItemView();
                if (parent == null) {
                    break;
                }
            }
            if (!parent) {
                return null;
            }
            return <LayoutComponentView>parent;
        }

        static getParentLayoutComponentView(pageComponentView: PageComponentView<PageComponent>): LayoutComponentView {

            var parentRegion = pageComponentView.getParentItemView();
            var potentialLayoutComponentView = parentRegion.getParentItemView()
            if (api.ObjectHelper.iFrameSafeInstanceOf(potentialLayoutComponentView, LayoutComponentView)) {
                return <LayoutComponentView> potentialLayoutComponentView;
            }
            else {
                return null;
            }
        }

        static hasParentLayoutComponentView(pageComponentView: PageComponentView<PageComponent>): boolean {
            return !LayoutComponentView.getParentLayoutComponentView(pageComponentView) ? false : true;
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