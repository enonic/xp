module app_browse {
    export class SpaceDetailPanel extends api_app_browse.DetailPanel {

        constructor() {
            super();
            var selectedSpaces = app.SpaceContext.get().getSelectedSpaces();
            if (!selectedSpaces || selectedSpaces.length == 0) {
                this.showBlank();
            }
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

        fireGridDeselectEvent(model:api_model.SpaceModel) {
            var models:api_model.SpaceModel[] = [];
            models.push(model);
            new GridDeselectEvent(models).fire();
        }
    }
}
