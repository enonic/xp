module app_browse_newcontent {

    export class SelectContentTypeAction extends api_ui.Action {

        private contentType:api_remote_contenttype.ContentTypeListNode;

        constructor(contentType?:api_remote_contenttype.ContentTypeListNode) {
            super("SelectContentType");
            this.contentType = contentType;
        }

        setContentType(contentType:api_remote_contenttype.ContentTypeListNode):SelectContentTypeAction {
            this.contentType = contentType;
            return this;
        }

        getContentType():api_remote_contenttype.ContentTypeListNode {
            return this.contentType;
        }

    }

}