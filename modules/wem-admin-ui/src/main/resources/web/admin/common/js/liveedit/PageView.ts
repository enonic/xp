module api.liveedit {

    import Content = api.content.Content;

    export class PageView extends ItemView {

        private content: Content;

        private regionViews: RegionView[] = [];

        constructor(content: Content, element?: HTMLElement, parent?: HTMLElement) {
            super(PageItemType.get(), element, false, parent);
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