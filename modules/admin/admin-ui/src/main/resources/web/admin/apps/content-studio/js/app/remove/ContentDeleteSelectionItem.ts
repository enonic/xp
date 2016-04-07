module app.remove {

    import ContentSummaryAndCompareStatus = api.content.ContentSummaryAndCompareStatus;
    import CompareStatus = api.content.CompareStatus;
    import BrowseItem = api.app.browse.BrowseItem;

    export class ContentDeleteSelectionItem extends api.app.browse.SelectionItem<ContentSummaryAndCompareStatus> {

        private statusDiv: api.dom.DivEl;

        constructor(viewer: api.ui.Viewer<ContentSummaryAndCompareStatus>, item: BrowseItem<ContentSummaryAndCompareStatus>, removeCallback?: () => void) {
           super(viewer, item, removeCallback);

           this.initStatusDiv(item.getModel().getCompareStatus());
        }

        doRender(): boolean {
            super.doRender();
            this.appendChild(this.statusDiv);

            return true;
        }

        private initStatusDiv(status: CompareStatus) {
            this.statusDiv = new api.dom.DivEl("status");
            this.statusDiv.setHtml(api.content.CompareStatusFormatter.formatStatus(status));
            var statusClass = "" + CompareStatus[status];
            this.statusDiv.addClass(statusClass.toLowerCase());
        }
    }
}