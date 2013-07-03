module app_browse {

    export class ContentBrowsePanel extends api_app_browse.BrowsePanel {

        private toolbar:ContentBrowseToolbar;

        private filterPanel:any;

        private grid:ContentTreeGridPanel;

        private browseItemPanel:app_browse.ContentBrowseItemPanel;

        constructor() {

            this.toolbar = new ContentBrowseToolbar();
            this.grid = components.gridPanel = new ContentTreeGridPanel('contentTreeGrid');
            this.browseItemPanel = components.detailPanel = new app_browse.ContentBrowseItemPanel();

            this.filterPanel = new Admin.view.contentManager.FilterPanel({
                region: 'west',
                width: 200
            });

            super(this.toolbar, this.grid, this.browseItemPanel, this.filterPanel);


            GridSelectionChangeEvent.on((event) => {

                var items:api_app_browse.BrowseItem[] = [];
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
                        var item = new api_app_browse.BrowseItem(models[index]).
                            setDisplayName(contentGet.displayName).
                            setPath(contentGet.path).
                            setIconUrl(contentGet.iconUrl);
                        items.push(item);
                    });
                    this.browseItemPanel.setItems(items);
                });
            });
        }
    }
}
