module app_ui {
    export class SpaceDetailPanel extends api_ui_detailpanel.DetailPanel {

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

            var tabPanel = new api_ui_detailpanel.DetailTabPanel(model);
            tabPanel.addTab(new api_ui_detailpanel.DetailPanelTab("Analytics"));
            tabPanel.addTab(new api_ui_detailpanel.DetailPanelTab("Sales"));
            tabPanel.addTab(new api_ui_detailpanel.DetailPanelTab("History"));

            var testAction = new api_action.Action("Test");
            tabPanel.addAction(testAction);

            this.getEl().appendChild(tabPanel.getHTMLElement());
        }

        private showMultiple(models:any[]) {
            this.empty();
            for (var i in models) {
                var removeCallback = (box:api_ui_detailpanel.DetailPanelBox) => {
                    var models:api_model.SpaceModel[] = [box.getModel()];
                    new app_event.GridDeselectEvent(models).fire();
                }
                this.getEl().appendChild(new api_ui_detailpanel.DetailPanelBox(models[i], removeCallback).getHTMLElement());
            }
        }
    }
}
