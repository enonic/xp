module app_wizard_event {

    export class BaseContentModelEvent extends api_event.Event {

        private model: api_content.ContentSummary[];

        constructor(name: string, model: api_content.ContentSummary[]) {
            this.model = model;
            super(name);
        }

        getModels(): api_content.ContentSummary[] {
            return this.model;
        }
    }
}
