module app_new {

    export class RecentContentTypesList extends api_dom.DivEl {

        private contentTypesList:ContentTypesList;

        constructor(className?:string) {
            super("RecentContentTypesList", className);

            var h4 = new api_dom.H4El();
            h4.getEl().setInnerHtml("Recent");
            this.appendChild(h4);

            this.contentTypesList = new ContentTypesList();

            this.appendChild(this.contentTypesList);
        }

        refresh() {
            this.contentTypesList.setNodes(RecentContentTypes.get().getRecentContentTypes());
        }

        getNodes():api_remote_contenttype.ContentTypeListNode[] {
            return this.contentTypesList.getNodes();
        }
    }

}