module app_new {

    export class AllContentTypesList extends ContentTypesList implements api_event.Observable {

        private input:api_dom.Element;

        constructor(className?:string) {
            super("RecentContentTypesList", "Recent", className);
        }

        createHeader(title:string) {
            this.input = new api_dom.Element("input");
            this.input.getEl().addEventListener("keyup", (event:Event) => {
                var value = (<HTMLInputElement> event.target).value;
                if(value) {
                    this.filter("displayName", value);
                } else {
                    this.clearFilter();
                }
            });
            this.appendChild(this.input);
        }
    }

}