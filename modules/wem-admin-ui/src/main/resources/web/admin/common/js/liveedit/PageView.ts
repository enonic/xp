module api.liveedit {

    import Content = api.content.Content;

    export class PageView extends ItemView {

        private content: Content;

        private regionViews: RegionView[] = [];

        constructor(content: Content, element?: HTMLElement, parent?: HTMLElement) {
            super(PageItemType.get(), element, false, parent);

            this.setContent(content);
        }

        getName(): string {

            return this.content.getDisplayName();
        }

        setContent(content: Content) {
            this.content = content;
            if (content) {
                this.setTooltipObject(content);
            }
        }

        getParentItemView(): ItemView {
            return null;
        }

        select() {
            new PageSelectEvent(this).fire();
            super.select();
        }

        getTooltipViewer(): api.ui.Viewer<api.content.ContentSummary> {
            return new api.content.ContentSummaryViewer();
        }

        addRegion(view: RegionView) {
            this.regionViews.push(view);
        }

        getRegions(): RegionView[] {
            return this.regionViews;
        }
    }
}