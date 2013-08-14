module api_app_browse_filter {

    export class FacetContainer extends api_dom.DivEl {

        private facetGroups:FacetGroup[] = [];
        private lastFacetGroup:FacetGroup;

        constructor(data?:FacetGroupParams[]) {
            super('FacetContainer');

            if (data) {
                for (var i = 0; i < data.length; i++) {
                    this.addFacetGroup(new FacetGroup(data[i]));
                }
            }

            api_event.FilterSearchEvent.on((event) => {
                if (event.getTarget()) {
                    this.lastFacetGroup = (<Facet>event.getTarget()).getFacetGroup();
                } else {
                    this.lastFacetGroup = undefined;
                }
            })
        }

        private addFacetGroup(facetGroup:FacetGroup) {
            this.facetGroups.push(facetGroup);
            this.appendChild(facetGroup);
        }

        private getFacetGroup(name:string) {
            for (var i = 0; i < this.facetGroups.length; i++) {
                var facetGroup:FacetGroup = this.facetGroups[i];
                if (facetGroup.getName() == name) {
                    return facetGroup;
                }
            }
            return null;
        }

        update(facetGroupsData:FacetGroupParams[]) {
            for (var i = 0; i < facetGroupsData.length; i++) {
                var facetGroupData = facetGroupsData[i];
                var facetGroup:FacetGroup = this.getFacetGroup(facetGroupData.name);

                if (facetGroup != null && facetGroup != this.lastFacetGroup) {
                    facetGroup.update(facetGroupData);
                } else if (facetGroup == null) {
                    this.addFacetGroup(new FacetGroup(facetGroupData));
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
            var facetGroup:FacetGroup;
            for (var i = 0; i < this.facetGroups.length; i++) {
                facetGroup = this.facetGroups[i];
                values[facetGroup.getName()] = facetGroup.getValues();
            }
            return values;
        }

        isDirty():bool {
            for (var i = 0; i < this.facetGroups.length; i++) {
                if (this.facetGroups[i].isDirty()) {
                    return true;
                }
            }
            return false;
        }

    }

}