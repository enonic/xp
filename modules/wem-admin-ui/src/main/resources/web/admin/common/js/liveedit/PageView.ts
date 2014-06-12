module api.liveedit {

    import Content = api.content.Content;
    import PageRegions = api.content.page.PageRegions;
    import Region = api.content.page.region.Region;

    export class PageViewBuilder {

        itemViewProducer: ItemViewIdProducer;

        pageRegions: PageRegions;

        content: Content;

        element: HTMLElement;

        setItemViewProducer(value: ItemViewIdProducer): PageViewBuilder {
            this.itemViewProducer = value;
            return this;
        }

        setPageRegions(value: PageRegions): PageViewBuilder {
            this.pageRegions = value;
            return this;
        }

        setContent(value: Content): PageViewBuilder {
            this.content = value;
            return this;
        }

        setElement(value: HTMLElement): PageViewBuilder {
            this.element = value;
            return this;
        }
    }

    export class PageView extends ItemView {

        private content: Content;

        private pageRegions: PageRegions;

        private regionViews: RegionView[] = [];

        constructor(builder: PageViewBuilder) {
            super(new ItemViewBuilder().
                setItemViewIdProducer(builder.itemViewProducer).
                setType(PageItemType.get()).
                setElement(builder.element));
            this.setContent(builder.content);
            this.pageRegions = builder.pageRegions;
            this.parseItemViews();
        }

        getName(): string {

            return this.content.getDisplayName();
        }

        private setContent(content: Content) {
            this.content = content;
            if (content) {
                this.setTooltipObject(content);
            }
        }

        getParentItemView(): ItemView {
            return null;
        }

        select() {
            new PageSelectEvent(this).fire();
            super.select();
        }

        getTooltipViewer(): api.ui.Viewer<api.content.ContentSummary> {
            return new api.content.ContentSummaryViewer();
        }

        addRegion(view: RegionView) {
            this.regionViews.push(view);
        }

        getRegions(): RegionView[] {
            return this.regionViews;
        }

        toItemViewArray(): ItemView[] {

            var array: ItemView[] = [];
            array.push(this);
            this.regionViews.forEach((regionView: RegionView) => {
                array = array.concat(regionView.toItemViewArray());
            });
            return array;
        }

        private parseItemViews() {

            this.doParseItemViews();
        }

        private doParseItemViews(parentElement?: api.dom.Element) {

            var regions: Region[] = this.pageRegions.getRegions();
            var children = parentElement ? parentElement.getChildren() : this.getChildren();
            var regionIndex = 0;
            children.forEach((element: api.dom.Element) => {
                var itemType = ItemType.fromElement(element);
                if (itemType) {
                    if (RegionItemType.get().equals(itemType)) {
                        var region = regions[regionIndex++];
                        var regionView = new RegionView(new RegionViewBuilder().
                            setParentView(this).
                            setRegion(region).
                            setElement(element.getHTMLElement()));
                        this.addRegion(regionView);
                    }
                    else {
                        this.doParseItemViews(parentElement);
                    }
                }
            });
        }
    }
}