module api.liveedit {

    export class PagePlaceholderInfoBlock extends api.dom.DivEl {

        private line1: api.dom.DivEl;

        private line2: api.dom.DivEl;

        constructor() {
            super("page-placeholder-info");

            this.line1 = new api.dom.DivEl("page-placeholder-info-line1");
            this.line2 = new api.dom.DivEl("page-placeholder-info-line2");

            this.appendChildren(this.line1, this.line2);
        }

        setTextForContent(type: string, name: string) {
            this.line1.setHtml('No page template supporting content type "' + type + ' '+ name + '"');
            this.line2.setHtml("Select a controller below to setup a customized page");
        }

        setNoControllersAvailableText() {
            this.line1.setHtml("No page controllers found");
            this.line2.setHtml("Please add an application to your site to support rendering of this item");
        }

    }
}