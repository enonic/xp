module api.liveedit {

    import RegionPath = api.content.page.RegionPath;
    import PageComponentView = api.liveedit.PageComponentView;

    export class RegionView extends ItemView {

        private pageComponents: PageComponentView[] = [];

        constructor(element?: HTMLElement) {
            super(RegionItemType.get(), element);
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
            this.pageComponents.push(view);
        }

        static isRegionViewFromHTMLElement(htmlElement: HTMLElement) : boolean {

            var path = htmlElement.getAttribute("data-live-edit-region");
            return !api.util.isStringBlank(path);
        }
    }
}