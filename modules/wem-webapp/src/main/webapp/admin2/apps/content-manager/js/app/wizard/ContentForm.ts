module app_wizard {

    export class ContentForm extends api_ui.Panel {

        private contentType:api_remote_contenttype.ContentType;

        constructor(contentType:api_remote_contenttype.ContentType) {
            super("ContentForm");

            this.contentType = contentType;

            var h1El = new api_dom.H1El();
            h1El.getEl().setInnerHtml("ContentForm: TODO");
            this.appendChild(h1El);
        }
    }
}
