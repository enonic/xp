module app_browse_newcontent {

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

            api_remote.RemoteService.contentType_list({}, function (result) => {
                this.contentTypesList.setNodes(result.contentTypes);
            });
        }

        setSelectAction(action:SelectContentTypeAction):AllContentTypesList {
            this.contentTypesList.setSelectAction(action);
            return this;
        }

    }

}