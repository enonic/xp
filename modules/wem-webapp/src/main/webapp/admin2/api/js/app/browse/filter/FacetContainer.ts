module api_app_browse_filter {

    export class FacetContainer extends api_dom.DivEl {

        facetGroups:FacetGroup[];

        constructor(data?:FacetGroupData[]) {
            super('FacetContainer');
            this.facetGroups = [];
            if (data) {
                for (var i = 0; i < data.length; i++) {
                    var facetGroup:FacetGroup = this.createFacetGroup(data[i]);
                    this.facetGroups.push(facetGroup);
                    this.appendChild(facetGroup);
                }
            }
        }

        private createFacetGroup(facetGroupData):FacetGroup {
            return new FacetGroup(facetGroupData);
        }

        addFacetGroup(facetGroup:FacetGroup) {
            this.facetGroups.push(facetGroup);
            this.appendChild(facetGroup);
        }

        reset() {
            for (var i in this.facetGroups) {
                this.facetGroups[i].reset();
            }
        }

        getFacetGroups():FacetGroup[] {
            return this.facetGroups;
        }

        getValues():any[] {
            var values = [];
            for (var i in this.facetGroups) {
                values[this.facetGroups[i].getName()] = this.facetGroups[i].getValues();
            }
            return values;
        }

        isDirty():bool {
            var isDirty:bool = false;
            for (var i = 0; i < this.facetGroups.length; i++) {
                if (this.facetGroups[i].isDirty()) {
                    isDirty = true;
                    break;
                }
            }
            return isDirty;
        }

    }

}