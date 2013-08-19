module api_app_browse_filter {

    export class ClearFilterButton extends api_dom.AEl {

        constructor() {
            super('ClearFilter', 'clear-filter-button');
            this.getEl().setInnerHtml('Clear filter');
            this.getHTMLElement().setAttribute('href', 'javascript:;');
        }
    }
}