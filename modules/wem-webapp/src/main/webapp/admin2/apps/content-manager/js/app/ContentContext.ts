module app {

    export class ContentContext  {

        private static context:ContentContext;

        private selectedContents:api_model.ContentModel[];

        static init():ContentContext{
            return context = new ContentContext();
        }

        static get():ContentContext{
            return context;
        }

        constructor(){
            app_event.GridSelectionChangeEvent.on((event) => {
                this.selectedContents = event.getModels();
            });
        }

        getSelectedContents():api_model.ContentModel[] {
            return this.selectedContents;
        }
    }
}
