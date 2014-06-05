module api.liveedit {

    import Region = api.content.page.region.Region;
    import RegionPath = api.content.page.RegionPath;
    import PageComponent = api.content.page.PageComponent;

    export class RegionView extends ItemView {

        private region: Region;

        private pageComponentViews: PageComponentView<PageComponent>[] = [];

        private placeholder: RegionPlaceholder;

        constructor(element?: HTMLElement) {
            super(RegionItemType.get(), element);

            this.placeholder = new RegionPlaceholder(this);

            PageComponentRemoveEvent.on((event: PageComponentRemoveEvent) => {
                this.removePageComponentView(event.getPageComponentView());
            });
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

        removePageComponentView(pageComponentView: PageComponentView<PageComponent>) {
            var indexToRemove = -1;
            this.pageComponentViews.forEach((curr: PageComponentView<PageComponent>, index: number) => {
                if (curr.getItemId().equals(pageComponentView.getItemId())) {
                    console.log("RegionView[" + this.getItemId().toNumber() + "].removePageComponentView: removing PageComponentView: " +
                                curr.getItemId().toString());
                    indexToRemove = index;
                }
            });

            if (indexToRemove >= 0) {
                this.pageComponentViews.splice(indexToRemove, 1);
                if (this.pageComponentViews.length == 0) {
                    console.log("RegionView[" + this.getItemId().toNumber() +
                                "].removePageComponentView: region is now empty, showing placeholder");
                    this.empty();
                }
            }
        }

        empty() {

            this.appendChild(this.placeholder);
        }

        isRegionEmpty(): boolean {

            var hasNotDropTargetPlaceholder: boolean = wemjq(this.getHTMLElement()).children('.live-edit-drop-target-placeholder').length ===
                                                       0;
            return this.pageComponentViews.length == 0 && hasNotDropTargetPlaceholder;
            //var hasNotParts: Boolean = regionElement.children('[data-live-edit-type]' + ':not(:hidden)').length === 0;
            //var hasNotDropTargetPlaceholder: Boolean = regionElement.children('.live-edit-drop-target-placeholder').length === 0;
            //return hasNotParts && hasNotDropTargetPlaceholder;
        }

        static isRegionViewFromHTMLElement(htmlElement: HTMLElement): boolean {

            var path = htmlElement.getAttribute("data-live-edit-region");
            return !api.util.isStringBlank(path);
        }
    }
}