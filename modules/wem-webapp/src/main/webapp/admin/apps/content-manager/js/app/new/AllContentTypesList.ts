module app_new {

    export class AllContentTypesList extends api_dom.DivEl implements api_event.Observable {

        private input:api_dom.Element;

        private contentTypesList:ContentTypesList;

        constructor(className?:string) {
            super("AllContentTypesList", className);

            this.input = new api_dom.Element("input");
            this.input.getEl().addEventListener("keyup", (event:Event) => {
                this.contentTypesList.filter("displayName", (<HTMLInputElement> event.target).value);
            });
            this.appendChild(this.input);

            this.contentTypesList = new ContentTypesList();
            this.appendChild(this.contentTypesList);
        }

        addListener(listener:ContentTypesListListener) {
            this.contentTypesList.addListener(listener);
        }

        removeListener(listener:ContentTypesListListener) {
            this.contentTypesList.removeListener(listener);
        }

        refresh() {

            var request = new api_schema_content.GetAllContentTypesRequest();
            request.send().done( (response:api_rest.JsonResponse<api_schema_content_json.ContentTypeSummaryListJson>) => {
                var contentTypes:api_schema_content.ContentTypeSummary[] = api_schema_content.ContentTypeSummary.fromJsonArray(response.getResult().contentTypes);
                this.contentTypesList.setContentTypes(contentTypes);
            } );
        }
    }

}