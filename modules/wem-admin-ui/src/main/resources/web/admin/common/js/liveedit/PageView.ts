module api.liveedit {

    import Content = api.content.Content;

    export class PageView extends ItemView {

        private regionViews: RegionView[] = [];

        constructor(element?: HTMLElement) {
            super(PageItemType.get(), element);
        }

        getName(): string {

            var content = PageItemType.get().getContent();
            return content ? content.getDisplayName() : "[No name]";
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