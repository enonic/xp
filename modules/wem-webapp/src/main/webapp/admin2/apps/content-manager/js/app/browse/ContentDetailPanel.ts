module app_browse {
    export class ContentDetailPanel extends api_app_browse.DetailPanel {

        constructor() {
            super();
            var selectedContents = app.ContentContext.get().getSelectedContents();
            if (!selectedContents || selectedContents.length == 0) {
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

        fireGridDeselectEvent(model:api_model.ContentModel) {
            var models:api_model.ContentModel[] = [];
            models.push(model);
            new GridDeselectEvent(models).fire();
        }
    }
}
