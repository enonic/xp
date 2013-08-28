module api_facet {

    export class TermsFacetEntryView extends api_dom.DivEl {

        private checkbox:api_dom.InputEl;

        private label:api_dom.LabelEl;

        private name:string;

        private facetGroup:TermsFacetView;

        constructor(facetData:TermsFacetEntry, facetGroup:TermsFacetView) {
            super('Facet', 'facet');
            this.name = facetData.name;

            this.facetGroup = facetGroup;
            this.checkbox = new api_dom.InputEl('FacetCheckbox', 'facet-cb', 'checkbox');
            this.label = new api_dom.LabelEl('', null, 'FacetLabel', 'facet-lbl');
            this.label.getEl().setInnerHtml(facetData.displayName + ' (' + facetData.count + ')');
            this.label.getEl().addEventListener('click', () => {
                var node = this.checkbox.getHTMLElement().getAttributeNode('checked');
                if (node) {
                    this.checkbox.getHTMLElement().removeAttribute('checked');
                } else {
                    this.checkbox.getHTMLElement().setAttribute('checked', '');
                }
                new api_app_browse_filter.FilterSearchEvent(this).fire();
            });
            this.appendChild(this.checkbox);
            this.appendChild(this.label);

            if (facetData.count == 0) {
                this.hide();
            }
        }

        getFacetGroup():TermsFacetView {
            return this.facetGroup;
        }

        getName():string {
            return this.name;
        }

        update(facetData:TermsFacetEntry) {
            this.label.getEl().setInnerHtml(facetData.displayName + ' (' + facetData.count + ')');
            if (facetData.count > 0 || this.isSelected()) {
                this.show();
            } else {
                this.hide();
            }
        }

        isSelected():bool {
            return this.checkbox.getHTMLElement().getAttributeNode('checked') != null;
        }

        reset() {
            this.checkbox.getHTMLElement().removeAttribute('checked');
        }

        isDirty():bool {
            if (this.checkbox.getHTMLElement().getAttributeNode('checked')) {
                return true;
            } else {
                return false;
            }
        }
    }

}