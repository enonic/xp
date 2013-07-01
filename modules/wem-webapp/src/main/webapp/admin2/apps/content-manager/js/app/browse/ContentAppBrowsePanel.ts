module app_browse {

    export class ContentAppBrowsePanel extends api_app_browse.AppBrowsePanel {

        private toolbar:ContentBrowseToolbar;

        private filterPanel:any;

        private grid:ContentTreeGridPanel;

        private detailPanel:ContentDetailPanel;

        constructor() {

            this.toolbar = new ContentBrowseToolbar();
            this.grid = components.gridPanel = new ContentTreeGridPanel('contentTreeGrid');
            this.detailPanel = components.detailPanel = new ContentDetailPanel();

            this.filterPanel = new Admin.view.contentManager.FilterPanel({
                region: 'west',
                width: 200
            });

            super(this.toolbar, this.grid, this.detailPanel, this.filterPanel);


            GridSelectionChangeEvent.on((event) => {

                var items:api_app_browse.DetailPanelItem[] = [];
                var models:api_model.ContentModel[] = event.getModels();
                var contentIds:string[] = [];
                models.forEach( (model:api_model.ContentModel) => {
                    contentIds.push(model.data.id);
                } );
                var getParams:api_remote.RemoteCallContentGetParams = {
                    contentIds: contentIds
                };
                api_remote.RemoteService.content_get(getParams, (result:api_remote.RemoteCallContentGetResult)=> {

                    result.content.forEach((contentGet:api_remote.ContentGet, index:number) => {
                        var item = new api_app_browse.DetailPanelItem(models[index]).
                            setDisplayName(contentGet.displayName).
                            setPath(contentGet.path).
                            setIconUrl(contentGet.iconUrl);
                        items.push(item);
                    });
                    this.detailPanel.setItems(items);
                });
            });
        }
    }
}
