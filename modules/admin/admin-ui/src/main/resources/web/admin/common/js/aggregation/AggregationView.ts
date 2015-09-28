module api.aggregation {

    export class AggregationView extends api.dom.DivEl {

        private parentGroupView: api.aggregation.AggregationGroupView;

        private aggregation: api.aggregation.Aggregation;

        private bucketSelectionChangedListeners: Function[] = [];

        displayNameMap: string[] = [];

        constructor(aggregation: api.aggregation.Aggregation, parentGroupView: api.aggregation.AggregationGroupView) {
            super('aggregation-view');
            this.aggregation = aggregation;
            this.parentGroupView = parentGroupView;
        }

        setDisplayNamesMap(displayNameMap: string[]): void {
            this.displayNameMap = displayNameMap;
            this.setDisplayNames();
        }

        setDisplayNames(): void {
            throw new Error("Must be implemented by inheritor");
        }

        getDisplayNameForName(name: string): string {
            return this.displayNameMap[name];
        }

        getAggregation(): api.aggregation.Aggregation {
            return this.aggregation;
        }

        getParentGroupView() {
            return this.parentGroupView;
        }

        getName(): string {
            return this.aggregation.getName();
        }

        deselectFacet(supressEvent?: boolean) {
            throw new Error("Must be implemented by inheritor");
        }

        hasSelectedEntry(): boolean {
            throw new Error("Must be implemented by inheritor");
        }

        getSelectedValues(): api.aggregation.Bucket[] {
            throw new Error("Must be implemented by inheritor");
        }

        update(aggregation: api.aggregation.Aggregation) {
            throw new Error("Must be implemented by inheritor");
        }


        onBucketViewSelectionChanged(listener: (event: api.aggregation.BucketViewSelectionChangedEvent) => void) {
            this.bucketSelectionChangedListeners.push(listener);
        }

        unBucketViewSelectionChanged(listener: (event: api.aggregation.BucketViewSelectionChangedEvent) => void) {
            this.bucketSelectionChangedListeners = this.bucketSelectionChangedListeners.filter(function (curr) {
                return curr != listener;
            });
        }

        notifyBucketViewSelectionChanged(event: api.aggregation.BucketViewSelectionChangedEvent) {

            this.bucketSelectionChangedListeners.forEach((listener: (event: api.aggregation.BucketViewSelectionChangedEvent) => void) => {
                listener(event);
            });
        }


        static createAggregationView(aggregation: api.aggregation.Aggregation,
                                     parentGroupView: api.aggregation.AggregationGroupView): api.aggregation.AggregationView {
            if (api.ObjectHelper.iFrameSafeInstanceOf(aggregation, api.aggregation.BucketAggregation)) {
                return new api.aggregation.BucketAggregationView(<api.aggregation.BucketAggregation>aggregation, parentGroupView);
            }
            else {
                throw Error("Creating AggregationView of this type of Aggregation is not supported: " + aggregation);
            }
        }
    }


}