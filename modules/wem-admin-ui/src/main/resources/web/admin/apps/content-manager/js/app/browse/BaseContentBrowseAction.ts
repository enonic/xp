module app.browse {
    
    import Action = api.ui.Action;

    export class BaseContentBrowseAction extends Action {

        constructor(label: string, shortcut?: string) {
            super(label, shortcut);
        }

        extModelsToContentSummaries(models: Ext_data_Model[]): api.content.ContentSummary[] {
            var summaries: api.content.ContentSummary[] = [];
            for (var i = 0; i < models.length; i++) {
                summaries.push(api.content.ContentSummary.fromJson(<api.content.json.ContentSummaryJson>models[i].data))
            }
            return summaries;
        }
    }
}
