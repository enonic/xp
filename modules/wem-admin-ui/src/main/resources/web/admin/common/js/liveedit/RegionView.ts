module api.liveedit {

    import Region = api.content.page.region.Region;
    import RegionPath = api.content.page.RegionPath;
    import PageComponent = api.content.page.PageComponent;

    export class RegionView extends ItemView {

        private region: Region;

        private pageComponentViews: PageComponentView<PageComponent>[] = [];

        constructor(element?: HTMLElement) {
            super(RegionItemType.get(), element);
        }

        setData(region: Region) {
            this.region = region;

            var components = region.getComponents();
            this.getPageComponents().forEach((view: PageComponentView<PageComponent>, index: number) => {
                var pageComponent = components[index];
                view.setPageComponent(pageComponent);
            });
        }

        getRegionName(): string {
            return this.getRegionPath().getRegionName();
        }

        getRegionPath(): RegionPath {
            var asString = this.getEl().getAttribute("data-live-edit-region");
            return RegionPath.fromString(asString);
        }

        getName(): string {

            return this.getRegionName().toString();
        }

        select() {
            new RegionSelectEvent(this.getRegionPath(), this).fire();
            super.select();
        }

        addPageComponent(view: PageComponentView<PageComponent>) {
            this.pageComponentViews.push(view);
        }

        getPageComponents(): PageComponentView<PageComponent>[] {
            return this.pageComponentViews;
        }

        static isRegionViewFromHTMLElement(htmlElement: HTMLElement): boolean {

            var path = htmlElement.getAttribute("data-live-edit-region");
            return !api.util.isStringBlank(path);
        }
    }
}