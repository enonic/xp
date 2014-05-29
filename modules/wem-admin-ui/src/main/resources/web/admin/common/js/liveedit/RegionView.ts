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

        getName(): string {

            return this.getRegionName().toString();
        }

        select() {
            new RegionSelectEvent(this.getRegionPath(), this).fire();
            super.select();
        }

        static fromHTMLElement(element: HTMLElement): RegionView {
            return new RegionView(element);
        }

        public static fromJQuery(element: JQuery): RegionView {
            return new RegionView(<HTMLElement>element.get(0));
        }
    }
}