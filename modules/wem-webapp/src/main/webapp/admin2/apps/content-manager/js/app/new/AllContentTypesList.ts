module app_new {

    export class AllContentTypesList extends api_dom.DivEl {

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

        addSelectedListener(listener:(selectedContentType:api_remote_contenttype.ContentType) => void) {
            this.contentTypesList.addSelectedListener(listener);
        }

        refresh() {

            api_remote_contenttype.RemoteContentTypeService.contentType_list({}, function (result) => {
                this.contentTypesList.setContentTypes(result.contentTypes);
            });
        }
    }

}