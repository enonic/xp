module api_facet {

    export class FacetGroupView extends api_dom.DivEl {

        private facetGroups:TermsFacetView[] = [];
        private lastFacetGroup:TermsFacetView;

        constructor(data?:TermsFacet[]) {
            super('FacetGroupView');

            if (data) {
                for (var i = 0; i < data.length; i++) {
                    this.addFacetGroup(new TermsFacetView(data[i]));
                }
            }

            api_app_browse_filter.FilterSearchEvent.on((event:api_app_browse_filter.FilterSearchEvent) => {
                if (event.getTarget()) {
                    this.lastFacetGroup = (<TermsFacetEntryView>event.getTarget()).getFacetGroup();
                } else {
                    this.lastFacetGroup = undefined;
                }
            });
        }

        private addFacetGroup(facetGroup:TermsFacetView) {
            this.facetGroups.push(facetGroup);
            this.appendChild(facetGroup);
        }

        private getFacetGroup(name:string) {
            for (var i = 0; i < this.facetGroups.length; i++) {
                var facetGroup:TermsFacetView = this.facetGroups[i];
                if (facetGroup.getName() == name) {
                    return facetGroup;
                }
            }
            return null;
        }

        update(facetGroupsData:api_facet.TermsFacet[]) {
            for (var i = 0; i < facetGroupsData.length; i++) {
                var termsFacet = facetGroupsData[i];
                var facetGroup:TermsFacetView = this.getFacetGroup(termsFacet.name);

                if (facetGroup != null && facetGroup != this.lastFacetGroup) {
                    facetGroup.update(termsFacet);
                } else if (facetGroup == null) {
                    facetGroup = new TermsFacetView(termsFacet);
                    this.addFacetGroup(facetGroup);
                }
            }
        }

        reset() {
            for (var i = 0; i < this.facetGroups.length; i++) {
                this.facetGroups[i].reset();
            }
            this.lastFacetGroup = undefined;
        }

        getValues():any[] {
            var values = [];
            var facetGroup:TermsFacetView;
            for (var i = 0; i < this.facetGroups.length; i++) {
                facetGroup = this.facetGroups[i];
                values[facetGroup.getName()] = facetGroup.getValues();
            }
            return values;
        }

        isDirty():boolean {
            for (var i = 0; i < this.facetGroups.length; i++) {
                if (this.facetGroups[i].isDirty()) {
                    return true;
                }
            }
            return false;
        }

    }

}