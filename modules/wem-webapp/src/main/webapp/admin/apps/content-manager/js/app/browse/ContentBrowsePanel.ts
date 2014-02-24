module app.browse {

    export class ContentBrowsePanel extends api.app.browse.BrowsePanel<api.content.ContentSummary> {

        private browseActions:app.browse.ContentBrowseActions;

        private toolbar:ContentBrowseToolbar;

        private contentTreeGridPanel:app.browse.ContentTreeGridPanel;

        private contentTreeGridPanel2:app.browse.grid.ContentGridPanel2;

        private contentFilterPanel:app.browse.filter.ContentBrowseFilterPanel;

        private contentBrowseItemPanel:ContentBrowseItemPanel;

        constructor() {
            var treeGridContextMenu = new ContentTreeGridContextMenu();
            this.contentTreeGridPanel = components.gridPanel = new app.browse.ContentTreeGridPanel({
                contextMenu: treeGridContextMenu
            });

            this.browseActions = ContentBrowseActions.init(this.contentTreeGridPanel);
            treeGridContextMenu.setActions(this.browseActions);

            this.toolbar = new ContentBrowseToolbar(this.browseActions);
            this.contentBrowseItemPanel =
            components.detailPanel = new ContentBrowseItemPanel();

            this.contentFilterPanel = new app.browse.filter.ContentBrowseFilterPanel();

            this.contentTreeGridPanel2 = new app.browse.grid.ContentGridPanel2();

            super({
                browseToolbar: this.toolbar,
                treeGridPanel: this.contentTreeGridPanel,
                treeGridPanel2: this.contentTreeGridPanel2,
                browseItemPanel: this.contentBrowseItemPanel,
                filterPanel: this.contentFilterPanel});

            ShowPreviewEvent.on((event) => {
                this.contentBrowseItemPanel.setPreviewMode(true);
            });

            ShowDetailsEvent.on((event) => {
                this.contentBrowseItemPanel.setPreviewMode(false);
            });

            api.content.ContentDeletedEvent.on((event) => {
                var contents:api.content.ContentSummary[] = event.getContents();
                for (var i = 0; i < contents.length; i++) {
                    this.contentTreeGridPanel.remove(contents[i].getPath().toString());
                }
            });

            api.content.ContentCreatedEvent.on((event) => {
                this.setRefreshNeeded(true);
            });

            api.content.ContentUpdatedEvent.on((event) => {
                this.setRefreshNeeded(true);
            });

            this.contentTreeGridPanel.addListener(<api.app.browse.grid.TreeGridPanelListener>{
                onSelectionChanged: (event:api.app.browse.grid.TreeGridSelectionChangedEvent) => {
                    var contentSummaries = this.extModelsToContentSummaries(event.selectedModels);
                    this.browseActions.updateActionsEnabledState(contentSummaries);
                }
            });

            ShowNewContentGridEvent.on( () => {
                super.toggleShowingNewGrid();
            });
        }

        onElementShown() {
            app.Router.setHash("browse");
        }

        getActions():api.ui.Action[] {
            var actions = super.getActions();
            // TODO: Ensures shortcut for showing new experimental content grid without having the action in the toolbar
            actions.push(app.browse.ContentBrowseActions.get().SHOW_NEW_CONTENT_GRID);
            return actions;
        }

        extModelsToContentSummaries(models:Ext_data_Model[]):api.content.ContentSummary[] {

            var summaries:api.content.ContentSummary[] = [];
            for (var i = 0; i < models.length; i++) {
                summaries.push(new api.content.ContentSummary(<api.content.json.ContentSummaryJson>models[i].data))
            }
            return summaries;
        }

        extModelsToBrowseItems(models:Ext_data_Model[]):api.app.browse.BrowseItem<api.content.ContentSummary>[] {

            var browseItems:api.app.browse.BrowseItem<api.content.ContentSummary>[] = [];
            models.forEach((model:Ext_data_Model) => {
                var content = new api.content.ContentSummary(<api.content.json.ContentSummaryJson>model.data);
                var item = new api.app.browse.BrowseItem<api.content.ContentSummary>(content).
                    setDisplayName(model.data['displayName']).
                    setPath(model.data['path']).
                    setIconUrl(model.data['iconUrl']);
                browseItems.push(item);
            });
            return browseItems;
        }
    }

}
