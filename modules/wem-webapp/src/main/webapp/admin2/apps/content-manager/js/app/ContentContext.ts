module app {

    export class ContentContext  {

        private static context:ContentContext;

        private selectedContents:api_model.ContentExtModel[];

        static init():ContentContext{
            return context = new ContentContext();
        }

        static get():ContentContext{
            return context;
        }

        constructor(){
            app_browse.GridSelectionChangeEvent.on((event) => {
                this.selectedContents = event.getModels();
            });
        }

        getSelectedContents():api_model.ContentExtModel[] {
            return this.selectedContents;
        }
    }
}
