module api.app.browse.filter {

    import i18n = api.util.i18n;

    export class ClearFilterButton extends api.dom.AEl {

        constructor() {
            super('clear-filter-button');
            this.getEl().setInnerHtml(i18n('panel.filter.clear'));
            this.getHTMLElement().setAttribute('href', 'javascript:;');
            this.hide();
        }
    }
}
