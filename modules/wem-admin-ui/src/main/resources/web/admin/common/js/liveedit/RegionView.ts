module api.liveedit {

    import Region = api.content.page.region.Region;
    import RegionPath = api.content.page.RegionPath;
    import PageComponent = api.content.page.PageComponent;

    export class RegionView extends ItemView {

        private parentView: RegionContainingView;

        private region: Region;

        private pageComponentViews: PageComponentView<PageComponent>[] = [];

        private placeholder: RegionPlaceholder;

        constructor(parentView: RegionContainingView, region: Region, element?: HTMLElement) {
            super(RegionItemType.get(), element);
            this.region = region;
            this.parentView = parentView;
            this.placeholder = new RegionPlaceholder(this);
            this.placeholder.hide();
            this.appendChild(this.placeholder);
        }

        getParentItemView(): ItemView {
            return <ItemView>this.parentView;
        }

        setData(region: Region) {
            this.region = region;

            var components = region.getComponents();
            this.getPageComponentViews().forEach((view: PageComponentView<PageComponent>, index: number) => {
                var pageComponent = components[index];
                view.setPageComponent(pageComponent);
            });
        }

        getRegion(): Region {
            return this.region;
        }

        getRegionName(): string {
            return this.getRegionPath().getRegionName();
        }

        getRegionPath(): RegionPath {

            return this.region.getPath();
        }

        getName(): string {

            return this.getRegionName().toString();
        }

        select() {
            new RegionSelectEvent(this.getRegionPath(), this).fire();
            super.select();
        }

        registerPageComponentView(view: PageComponentView<PageComponent>) {
            this.pageComponentViews.push(view);
            this.placeholder.hide();
        }

        addPageComponentView(view: PageComponentView<PageComponent>, index: number) {
            this.pageComponentViews.splice(index, 0, view);
            this.insertChild(view, index);
            this.placeholder.hide();
        }

        getPageComponentViews(): PageComponentView<PageComponent>[] {
            return this.pageComponentViews;
        }

        removePageComponentView(pageComponentView: PageComponentView<PageComponent>) {

            pageComponentView.getElement().remove();

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
                    this.placeholder.show();
                }
            }
        }

        refreshPlaceholder() {
            if (this.pageComponentViews.length == 0) {
                this.placeholder.show();
            }
            else {
                this.placeholder.hide();
            }
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

            var type = htmlElement.getAttribute("data-" + ItemType.DATA_ATTRIBUTE);
            if (api.util.isStringBlank(type)) {
                return false;
            }
            return type == "region";
        }
    }
}