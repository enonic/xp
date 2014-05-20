module api.liveedit {

    import RegionPath = api.content.page.RegionPath;

    export class RegionView extends ItemView {

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

        static fromHTMLElement(element: HTMLElement): RegionView {
            return new RegionView(element);
        }
    }
}