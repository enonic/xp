module api_facet {

    export class TermsFacetView extends FacetView {

        private termsFacet:TermsFacet;

        private entryViews:TermsFacetEntryView[] = [];

        private hasAnyCountLargerThanZero:boolean;

        constructor(termsFacet:TermsFacet, parentGroupView:FacetGroupView) {
            super(termsFacet, parentGroupView);

            this.termsFacet = termsFacet;

            this.hasAnyCountLargerThanZero = false;
            this.termsFacet.getTermsFacetEntries().forEach((entry:TermsFacetEntry) => {
                this.addFacet(new TermsFacetEntryView(entry, this));
                if (entry.getCount() > 0) {
                    this.hasAnyCountLargerThanZero = true;
                }
            });

            if (!this.hasAnyCountLargerThanZero) {
                this.hide();
            }
        }

        private addFacet(entryView:TermsFacetEntryView) {
            this.appendChild(entryView);
            entryView.addSelectionChangeListener((event:FacetEntryViewSelectionChangedEvent) => {
                    this.notifyFacetEntrySelectionChanged(event);
                }
            );
            this.entryViews.push(entryView);
        }

        update(facet:Facet) {

            this.termsFacet = <TermsFacet>facet;

            var anyCountLargerThanZero = false;
            this.termsFacet.getTermsFacetEntries().forEach((entry:TermsFacetEntry) => {
                var existingEntry:TermsFacetEntryView = this.getFacetEntryView(entry.getName());
                if (existingEntry != null) {
                    existingEntry.update(entry);
                }
                if (entry.getCount() > 0) {
                    anyCountLargerThanZero = true;
                }
            });
            this.hasAnyCountLargerThanZero = anyCountLargerThanZero;

            if (!this.hasAnyCountLargerThanZero ) {
                this.hide();
            }
            else if (!this.isVisible()) {
                this.show();
            }
        }

        deselectFacet(supressEvent?:boolean) {
            this.entryViews.forEach((entryView:TermsFacetEntryView) => {
                entryView.deselect(supressEvent);
            });
        }

        hasSelectedEntry():boolean {
            var isSelected:boolean = false;
            this.entryViews.forEach((entryView:TermsFacetEntryView) => {
                if (entryView.isSelected()) {
                    isSelected = true;
                }
            });
            return isSelected;
        }

        getSelectedValues():string[] {
            var terms:string[] = [];
            this.entryViews.forEach((entryView:TermsFacetEntryView) => {
                if (entryView.isSelected()) {
                    terms.push(entryView.getName());
                }
            });
            return terms;
        }

        private getFacetEntryView(name:string):TermsFacetEntryView {
            for (var i = 0; i < this.entryViews.length; i++) {
                var facet:TermsFacetEntryView = this.entryViews[i];
                if (facet.getName() == name) {
                    return facet;
                }
            }
            return null;
        }

    }

}