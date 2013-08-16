module api_app_browse_filter {


    export class TextSearchField extends api_dom.InputEl {

        constructor(placeholder?:string) {
            super('SearchField', 'text-search-field');
            this.setPlaceholder(placeholder);
        }

        setPlaceholder(placeholder:string) {
            this.getEl().setAttribute('placeholder', placeholder);
        }

        addSearchListener(listener:(event:any)=>void) {
            this.getEl().addEventListener('keydown', listener);
        }
    }
}