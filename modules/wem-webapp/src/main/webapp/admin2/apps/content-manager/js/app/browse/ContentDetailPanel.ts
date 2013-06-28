module app_browse {
    export class ContentDetailPanel extends api_app_browse.DetailPanel {

        constructor() {
            super();
            var selectedContents = app.ContentContext.get().getSelectedContents();
            if (!selectedContents || selectedContents.length == 0) {
                this.showBlank();
            }

            GridSelectionChangeEvent.on((event) => {
                this.update(event.getModels());
            });

        }

        showBlank() {
            this.getEl().setInnerHtml("Nothing selected");
        }

        update(models:any[]) {
            if (models.length == 1) {
                this.showSingle(models[0]);
            } else if (models.length > 1) {
                this.showMultiple(models);
            }
        }

        private showSingle(model) {
            this.empty();

            var tabPanel = new api_app_browse.DetailTabPanel(model);
            tabPanel.addTab(new api_app_browse.DetailPanelTab("Analytics"));
            tabPanel.addTab(new api_app_browse.DetailPanelTab("Sales"));
            tabPanel.addTab(new api_app_browse.DetailPanelTab("History"));

            tabPanel.addAction(app_browse.ContentBrowseActions.NEW_CONTENT);
            tabPanel.addAction(app_browse.ContentBrowseActions.EDIT_CONTENT);
            tabPanel.addAction(app_browse.ContentBrowseActions.OPEN_CONTENT);
            tabPanel.addAction(app_browse.ContentBrowseActions.DELETE_CONTENT);
            tabPanel.addAction(app_browse.ContentBrowseActions.DUPLICATE_CONTENT);
            tabPanel.addAction(app_browse.ContentBrowseActions.MOVE_CONTENT);

            this.getEl().appendChild(tabPanel.getHTMLElement());
        }

        private showMultiple(models:any[]) {
            this.empty();
            for (var i in models) {
                var removeCallback = (box:api_app_browse.DetailPanelBox) => {
                    var models:api_model.ContentModel[] = [box.getModel()];
                    new GridDeselectEvent(models).fire();
                }
                this.getEl().appendChild(new api_app_browse.DetailPanelBox(models[i], removeCallback).getHTMLElement());
            }
        }
    }
}
