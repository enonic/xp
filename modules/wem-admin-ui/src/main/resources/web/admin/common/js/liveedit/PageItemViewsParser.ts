module api.liveedit {

    import Body = api.dom.Body;
    import PageComponentType = api.content.page.PageComponentType;
    import ComponentPath2 = api.content.page.ComponentPath2;
    import RegionPath2 = api.content.page.RegionPath2;

    export class PageItemViewsParser {

        private body: Body;

        private itemViews: ItemView[] = [];

        private pageItemViews: PageItemViews;

        constructor(body: Body) {
            this.body = body;
        }

        parse(): PageItemViews {

            var pageView = new PageView(this.body.getHTMLElement());
            this.itemViews.push(pageView);
            this.parsePageRegions(this.body, pageView);

            this.pageItemViews = new PageItemViews(pageView, this.itemViews);
            return this.pageItemViews;
        }

        private parsePageRegions(parent: api.dom.Element, pageView: PageView) {

            var children = parent.getChildren();
            children.forEach((element: api.dom.Element) => {
                var type = element.getEl().getData("live-edit-type");
                if (type == "region") {

                    var regionView = new RegionView(element.getHTMLElement());
                    this.itemViews.push(regionView);
                    pageView.addRegion(regionView);

                    this.parsePageComponents(element, regionView);
                }
                else {
                    this.parsePageRegions(element, pageView);
                }
            });
        }

        private parsePageComponents(parent: api.dom.Element, regionView: RegionView) {

            var children = parent.getChildren();
            children.forEach((element: api.dom.Element) => {
                var itemType = ItemType.byShortName(element.getEl().getData("live-edit-type"));
                api.util.assert(itemType.isPageComponentType(),
                        "Expected item beneath a Region to be a PageComponent: " + itemType.getShortName());


                var pageComponentView = <PageComponentView>itemType.createView(element.getHTMLElement());
                this.itemViews.push(pageComponentView);
                regionView.addPageComponent(pageComponentView);

                if (itemType.equals(api.liveedit.layout.LayoutItemType.get())) {
                    this.parseLayoutRegions(element, <api.liveedit.layout.LayoutView>pageComponentView);
                }
                else if (itemType.equals(api.liveedit.part.PartItemType.get())) {
                    this.parseContent(element, <api.liveedit.part.PartView>pageComponentView);
                }
            });
        }

        private parseLayoutRegions(parent: api.dom.Element, layoutView: api.liveedit.layout.LayoutView) {

            var children = parent.getChildren();
            children.forEach((element: api.dom.Element) => {
                var type = element.getEl().getData("live-edit-type");
                if (type == "region") {

                    var regionView = new RegionView(element.getHTMLElement());
                    this.itemViews.push(regionView);
                    layoutView.addRegion(regionView);

                    this.parsePageComponents(element, regionView);
                }
                else {
                    this.parseLayoutRegions(element, layoutView);
                }
            });
        }

        private parseContent(parent: api.dom.Element, partView: api.liveedit.part.PartView) {

            var children = parent.getChildren();
            children.forEach((element: api.dom.Element) => {
                var type = element.getEl().getData("live-edit-type");
                if (type == "content") {

                    var contentView = new ContentView(element.getHTMLElement());
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
