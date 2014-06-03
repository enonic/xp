module api.liveedit {

    import Region = api.content.page.region.Region;
    import RegionPath = api.content.page.RegionPath;

    export class RegionView extends ItemView {

        private region: Region;

        private pageComponentViews: PageComponentView[] = [];

        constructor(element?: HTMLElement) {
            super(RegionItemType.get(), element);
        }

        setData(region: Region) {
            this.region = region;

            var components = region.getComponents();
            this.getPageComponents().forEach((view: PageComponentView, index: number) => {
                var pageComponent = components[index];
                view.setData(pageComponent);
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

        addPageComponent(view: PageComponentView) {
            this.pageComponentViews.push(view);
        }

        getPageComponents(): PageComponentView[] {
            return this.pageComponentViews;
        }

        static isRegionViewFromHTMLElement(htmlElement: HTMLElement): boolean {

            var path = htmlElement.getAttribute("data-live-edit-region");
            return !api.util.isStringBlank(path);
        }
    }
}