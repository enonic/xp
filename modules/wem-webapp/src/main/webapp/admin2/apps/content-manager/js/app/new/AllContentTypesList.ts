module app_new {

    export class AllContentTypesList extends api_dom.DivEl implements api_ui.Observable {

        private input:api_dom.Element;

        private contentTypesList:ContentTypesList;

        constructor(className?:string) {
            super("AllContentTypesList", className);

            this.input = new api_dom.Element("input");
            this.input.getEl().addEventListener("keyup", function (event:Event) => {
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

            api_remote_contenttype.RemoteContentTypeService.contentType_list({}, function (result) => {
                this.contentTypesList.setContentTypes(result.contentTypes);
            });
        }
    }

}