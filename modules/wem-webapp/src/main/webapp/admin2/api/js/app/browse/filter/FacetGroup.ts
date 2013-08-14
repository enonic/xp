module api_app_browse_filter {

    export interface FacetGroupParams {
        name:string;
        displayName:string;
        terms:FacetData[];
    }

    export class FacetGroup extends api_dom.DivEl {

        private facets:Facet[] = [];

        private name:string;

        constructor(facetGroupParams:FacetGroupParams) {
            super('FacetGroup', 'facet-group');
            this.name = facetGroupParams.name;

            var facetTitle:api_dom.H2El = new api_dom.H2El('FacetTitle');
            facetTitle.getEl().setInnerHtml(facetGroupParams.displayName || facetGroupParams.name);
            this.appendChild(facetTitle);

            var needHide = true;
            for (var i = 0; i < facetGroupParams.terms.length; i++) {
                var facetData = facetGroupParams.terms[i];
                if (facetData.count > 0) {
                    needHide = false;
                }
                this.addFacet(new Facet(facetData, this));
            }

            if (needHide) {
                this.hide();
            }
        }

        private addFacet(facet:Facet) {
            this.appendChild(facet);
            this.facets.push(facet);
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

        update(facetGroupParams:FacetGroupParams) {
            var needHide = true;
            for (var i = 0; i < facetGroupParams.terms.length; i++) {
                var facetData = facetGroupParams.terms[i];
                if (facetData.count > 0) {
                    needHide = false;
                }
                var facet:Facet = this.getFacet(facetData.name);
                if (facet != null) {
                    facet.update(facetData);
                } else {
                    this.addFacet(new Facet(facetData, this));
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