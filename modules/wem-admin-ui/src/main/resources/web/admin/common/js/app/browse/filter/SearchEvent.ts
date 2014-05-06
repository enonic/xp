module api.app.browse.filter {

    export class SearchEvent {

        private searchInputValues: api.query.SearchInputValues;
        private elementChanged: api.dom.Element;

        constructor(searchInputValues: api.query.SearchInputValues, elementChanged?: api.dom.Element) {
            this.searchInputValues = searchInputValues;
            this.elementChanged = elementChanged;
        }

        getSearchInputValues(): api.query.SearchInputValues {
            return this.searchInputValues;
        }

        getElementChanged(): api.dom.Element {
            return this.elementChanged;
        }
    }
}