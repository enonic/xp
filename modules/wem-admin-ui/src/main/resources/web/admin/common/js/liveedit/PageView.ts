module api.liveedit {

    import Content = api.content.Content;

    export class PageView extends ItemView implements RegionContainingView {

        private regionViews: RegionView[] = [];

        constructor(element?: HTMLElement) {
            super(PageItemType.get(), element);
        }

        getName(): string {

            var content = PageItemType.get().getContent();
            return content ? content.getDisplayName() : "[No name]";
        }

        getParentItemView(): ItemView {
            return null;
        }

        select() {
            new PageSelectEvent(this).fire();
            super.select();
        }

        addRegion(view: RegionView) {
            this.regionViews.push(view);
        }

        getRegions(): RegionView[] {
            return this.regionViews;
        }
    }
}