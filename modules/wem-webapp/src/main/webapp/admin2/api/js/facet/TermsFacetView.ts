module api_facet {

    export class TermsFacetView extends api_dom.DivEl {

        private facets:TermsFacetEntryView[] = [];

        private name:string;

        constructor(facetGroupData:TermsFacet) {
            super('FacetGroup', 'facet-group');
            this.name = facetGroupData.name;

            var facetTitle:api_dom.H2El = new api_dom.H2El('FacetTitle');
            facetTitle.getEl().setInnerHtml(facetGroupData.displayName || facetGroupData.name);
            this.appendChild(facetTitle);

            var needHide = true;
            for (var i = 0; i < facetGroupData.terms.length; i++) {
                var facetData = facetGroupData.terms[i];
                if (facetData.count > 0) {
                    needHide = false;
                }
                this.addFacet(new TermsFacetEntryView(facetData, this));
            }

            if (needHide) {
                this.hide();
            }
        }

        private addFacet(facet:TermsFacetEntryView) {
            this.appendChild(facet);
            this.facets.push(facet);
        }

        private getFacet(name:string):TermsFacetEntryView {
            for (var i = 0; i < this.facets.length; i++) {
                var facet:TermsFacetEntryView = this.facets[i];
                if (facet.getName() == name) {
                    return facet;
                }
            }
            return null;
        }

        update(facetGroupData:TermsFacet) {
            var needHide = true;
            for (var i = 0; i < facetGroupData.terms.length; i++) {
                var facetData = facetGroupData.terms[i];
                if (facetData.count > 0) {
                    needHide = false;
                }
                var facet:TermsFacetEntryView = this.getFacet(facetData.name);
                if (facet != null) {
                    facet.update(facetData);
                } else {
                    this.addFacet(new TermsFacetEntryView(facetData, this));
                }
            }

            if (needHide) {
                this.hide();
            } else if (!this.isVisible()) {
                this.show();
            }
        }

        reset() {
            for (var i = 0; i < this.facets.length; i++) {
                this.facets[i].reset();
            }
        }

        isDirty():boolean {
            var isDirty:boolean = false;
            for (var i = 0; i < this.facets.length; i++) {
                if (this.facets[i].isDirty()) {
                    isDirty = true;
                    break;
                }
            }
            return isDirty;
        }

        getName():string {
            return this.name;
        }

        getValues() {
            var values = [];
            for (var i = 0; i < this.facets.length; i++) {
                var facet = this.facets[i];
                if (facet.isSelected()) {
                    values.push(facet.getName());
                }
            }
            return values;
        }

    }

}