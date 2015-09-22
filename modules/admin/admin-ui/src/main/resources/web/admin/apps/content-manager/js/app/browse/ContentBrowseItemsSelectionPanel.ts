module app.browse {

    import BrowseItem = api.app.browse.BrowseItem;
    import ContentSummary = api.content.ContentSummary;
    import ContentSummaryViewer = api.content.ContentSummaryViewer;

    export class ContentBrowseItemsSelectionPanel extends api.app.browse.BrowseItemsSelectionPanel<ContentSummary> {

        createItemViewer(item: BrowseItem<ContentSummary>): ContentSummaryViewer  {
            var viewer = new ContentSummaryViewer();
            viewer.setObject(item.getModel());
            return viewer;
        }

    }

}
