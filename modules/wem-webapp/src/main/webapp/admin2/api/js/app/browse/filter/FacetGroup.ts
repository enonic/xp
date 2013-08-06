module api_app_browse_filter {

    export interface FacetGroupData {
        name:string;
        displayName:string;
        terms:FacetData[];
    }

    export class FacetGroup extends api_dom.DivEl {
        facets;
        name:string;

        constructor(facetGroupData:FacetGroupData) {
            super('FacetGroup', 'facet-group');
            var facetTitle:api_dom.H2El = new api_dom.H2El('FacetTitle');
            this.facets = [];
            this.name = facetGroupData.name;
            facetTitle.getEl().setInnerHtml(facetGroupData.displayName || facetGroupData.name);
            this.appendChild(facetTitle);
            this.updateFacets(facetGroupData);
        }

        addFacet(facet:Facet) {
            this.appendChild(facet);
            this.facets.push(facet);
        }

        updateFacets(facetGroupData:FacetGroupData) {
            var isHidden = true;
            for (var i = 0; i < facetGroupData.terms.length; i++) {
                var facetData = facetGroupData.terms[i];
                if (facetData.count > 0) {
                    isHidden = false;
                }
                var facet:Facet = this.getFacet(facetData.name);
                if (facet != null) {
                    facet.update(facetData);
                } else {
                    facet = new Facet(facetData, this);
                    this.addFacet(facet);
                }

            }

            if (isHidden) {
                this.hide();
            } else {
                this.show();
            }
        }

        private getFacet(name:string):Facet {
            for (var i = 0; i < this.facets.length; i++) {
                var facet:Facet = this.facets[i];
                if (facet.getName() == name) {
                    return facet;
                }
            }
            return null;
        }

        reset() {
            for (var i = 0; i < this.facets.length; i++) {
                this.facets[i].reset();
            }
        }

        isDirty():bool {
            var isDirty:bool = false;
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