module app_new {

    export class RecommendedContentTypesList extends api_dom.DivEl {

        private contentTypesList:ContentTypesList;

        constructor(className?:string) {
            super("RecommendedContentTypesList", className);

            var h4 = new api_dom.H4El();
            h4.getEl().setInnerHtml("Recommended");
            this.appendChild(h4);

            this.contentTypesList = new ContentTypesList();
            this.appendChild(this.contentTypesList);
        }

        refresh() {
            this.contentTypesList.setNodes(RecentContentTypes.get().recommendContentTypes());
        }

        getNodes():api_remote_contenttype.ContentTypeListNode[] {
            return this.contentTypesList.getNodes();
        }



    }


}