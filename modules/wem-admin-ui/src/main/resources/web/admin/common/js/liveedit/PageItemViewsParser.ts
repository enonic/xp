module api.liveedit {

    import Body = api.dom.Body;
    import PageComponentType = api.content.page.PageComponentType;
    import Region = api.content.page.region.Region;
    import Content = api.content.Content;
    import ComponentPath2 = api.content.page.ComponentPath2;
    import RegionPath2 = api.content.page.RegionPath2;
    import PageComponent = api.content.page.PageComponent;
    import LayoutComponent = api.content.page.layout.LayoutComponent;
    import PageRegions = api.content.page.PageRegions;
    import PartView = api.liveedit.part.PartView;

    export class PageItemViewsParser {

        private body: Body;

        private itemViewIdProducer: ItemViewIdProducer;

        private content: Content;

        private pageRegions: PageRegions;

        private pageItemViews: PageItemViews;

        constructor(body: Body, itemViewIdProducer: ItemViewIdProducer, content: Content, pageRegions: PageRegions) {
            this.body = body;
            this.itemViewIdProducer = itemViewIdProducer;
            this.content = content;
            this.pageRegions = pageRegions;
        }

        parse(): PageItemViews {

            var pageView = new PageView(new PageViewBuilder().
                setItemViewProducer(this.itemViewIdProducer).
                setPageRegions(this.pageRegions).
                setContent(this.content).
                setElement(this.body.getHTMLElement()));

            this.pageItemViews = new PageItemViews(pageView);
            return this.pageItemViews;
        }
    }
}
