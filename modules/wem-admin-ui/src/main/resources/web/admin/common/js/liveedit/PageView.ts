module api.liveedit {

    import Content = api.content.Content;

    export class PageView extends ItemView implements RegionContainingView {

        private content: Content;

        private regionViews: RegionView[] = [];

        constructor(content: Content, element?: HTMLElement) {
            super(PageItemType.get(), element);
            this.content = content;
        }

        getName(): string {

            return this.content.getDisplayName();
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