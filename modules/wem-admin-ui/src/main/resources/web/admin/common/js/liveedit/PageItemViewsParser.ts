module api.liveedit {

    import Body = api.dom.Body;
    import PageComponentType = api.content.page.PageComponentType;
    import Region = api.content.page.region.Region;
    import ComponentPath2 = api.content.page.ComponentPath2;
    import RegionPath2 = api.content.page.RegionPath2;
    import PageComponent = api.content.page.PageComponent;
    import LayoutComponent = api.content.page.layout.LayoutComponent;
    import PageRegions = api.content.page.PageRegions;

    export class PageItemViewsParser {

        private body: Body;

        private pageRegions: PageRegions;

        private itemViews: ItemView[] = [];

        private pageItemViews: PageItemViews;

        constructor(body: Body, pageRegions: PageRegions) {
            this.body = body;
            this.pageRegions = pageRegions;
        }

        parse(): PageItemViews {

            var pageView = new PageView(this.body.getHTMLElement());
            this.itemViews.push(pageView);
            this.parsePageRegions(this.body, pageView);

            this.pageItemViews = new PageItemViews(pageView, this.itemViews);
            return this.pageItemViews;
        }

        private parsePageRegions(parent: api.dom.Element, pageView: PageView) {

            var regions: Region[] = this.pageRegions.getRegions();
            var children = parent.getChildren();
            var regionIndex = 0;
            children.forEach((element: api.dom.Element) => {
                var type = element.getEl().getData("live-edit-type");
                if (type == "region") {
                    var region = regions[regionIndex++];
                    var regionView = new RegionView(pageView, region, element.getHTMLElement());
                    this.itemViews.push(regionView);
                    pageView.addRegion(regionView);

                    this.parsePageComponents(element, regionView, region);
                }
                else {
                    this.parsePageRegions(element, pageView);
                }
            });
        }

        private parsePageComponents(parent: api.dom.Element, regionView: RegionView, region: Region) {

            var children = parent.getChildren();
            var pageComponentCount = 0;
            children.forEach((element: api.dom.Element) => {
                var itemType = ItemType.byShortName(element.getEl().getData("live-edit-type"));
                api.util.assert(itemType.isPageComponentType(),
                        "Expected item beneath a Region to be a PageComponent: " + itemType.getShortName());

                var pageComponent = region.getComponentByIndex(pageComponentCount++);
                var pageComponentView = <PageComponentView<PageComponent>>itemType.createView(regionView, pageComponent,
                    element.getHTMLElement());
                pageComponentView.setPageComponent(pageComponent);
                this.itemViews.push(pageComponentView);
                regionView.addPageComponent(pageComponentView);

                if (itemType.equals(api.liveedit.layout.LayoutItemType.get())) {
                    var layoutComponent = <LayoutComponent> pageComponent;
                    this.parseLayoutRegions(element, <api.liveedit.layout.LayoutView>pageComponentView, layoutComponent);
                }
                else if (itemType.equals(api.liveedit.part.PartItemType.get())) {
                    this.parseContent(element, <api.liveedit.part.PartView>pageComponentView);
                }
            });
        }

        private parseLayoutRegions(parent: api.dom.Element, layoutView: api.liveedit.layout.LayoutView, layoutComponent: LayoutComponent) {

            var regions: Region[] = layoutComponent.getLayoutRegions().getRegions();
            var children = parent.getChildren();
            var regionIndex = 0;
            children.forEach((element: api.dom.Element) => {
                var type = element.getEl().getData("live-edit-type");
                if (type == "region") {
                    var region = regions[regionIndex++];
                    var regionView = new RegionView(layoutView, region, element.getHTMLElement());
                    this.itemViews.push(regionView);
                    layoutView.addRegion(regionView);

                    this.parsePageComponents(element, regionView, region);
                }
                else {
                    this.parseLayoutRegions(element, layoutView, layoutComponent);
                }
            });
        }

        private parseContent(parent: api.dom.Element, partView: api.liveedit.part.PartView) {

            var children = parent.getChildren();
            children.forEach((element: api.dom.Element) => {
                var type = element.getEl().getData("live-edit-type");
                if (type == "content") {

                    var contentView = new ContentView(partView, element.getHTMLElement());
                    this.itemViews.push(contentView);
                    partView.addContent(contentView);
                }
                else {
                    this.parseContent(element, partView);
                }
            });
        }
    }
}
