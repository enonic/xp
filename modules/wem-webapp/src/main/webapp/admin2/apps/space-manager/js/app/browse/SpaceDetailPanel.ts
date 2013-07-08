module app_browse {
    export class SpaceDetailPanel extends api_app_browse.DetailPanel {

        constructor() {
            super();
            var selectedSpaces = app.SpaceContext.get().getSelectedSpaces();
            if (!selectedSpaces || selectedSpaces.length == 0) {
                this.showBlank();
            }

            app_event.GridSelectionChangeEvent.on((event) => {
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
            var actionMenu = new api_ui_menu.ActionMenu(SpaceBrowseActions.OPEN_SPACE, SpaceBrowseActions.EDIT_SPACE);
            var tabPanel = new api_app_browse.DetailTabPanel(model, actionMenu);

            tabPanel.addTab(new api_app_browse.DetailPanelTab("Analytics"));
            tabPanel.addTab(new api_app_browse.DetailPanelTab("Sales"));
            tabPanel.addTab(new api_app_browse.DetailPanelTab("History"));


            this.getEl().appendChild(tabPanel.getHTMLElement());
        }

        private showMultiple(models:any[]) {
            this.empty();
            for (var i in models) {
                var removeCallback = (box:api_app_browse.DetailPanelBox) => {
                    var models:api_model.SpaceModel[] = [box.getModel()];
                    new app_event.GridDeselectEvent(models).fire();
                }
                this.getEl().appendChild(new api_app_browse.DetailPanelBox(models[i], removeCallback).getHTMLElement());
            }
        }
    }
}
