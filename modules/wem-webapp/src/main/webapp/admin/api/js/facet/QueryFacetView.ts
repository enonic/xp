module api_facet {

    export class QueryFacetView extends FacetView {

        private queryFacet:QueryFacet;

        private entryView:QueryFacetEntryView;

        constructor(queryFacet:QueryFacet, parentGroupView:FacetGroupView) {
            super(queryFacet, parentGroupView);

            this.queryFacet = queryFacet;

            var needHide = true;

            this.entryView = new QueryFacetEntryView(queryFacet, this);
            this.appendChild(this.entryView);
            this.entryView.addSelectionChangeListener((event:FacetEntryViewSelectionChangedEvent) => {
                    this.notifyFacetEntrySelectionChanged(event);
                }
            );
            if (this.queryFacet.getCount() > 0) {
                needHide = false;
            }

            if (needHide) {
                this.hide();
            }
        }

        update(facet:Facet) {

            this.queryFacet = <QueryFacet>facet;
            this.entryView.update(this.queryFacet);

            var hasAnyCountLargerThanZero = this.queryFacet.getCount() > 0;
            if (!hasAnyCountLargerThanZero) {
                this.hide();
            }
            else if (!this.isVisible()) {
                this.show();
            }
        }

        deselectFacet() {
            this.entryView.deselect();
        }

        hasSelectedEntry():boolean {
            return this.entryView.isSelected();
        }

        getSelectedValues():string[] {
            var terms:string[] = [];
            terms.push(this.entryView.getName());
            return terms;
        }

    }

}