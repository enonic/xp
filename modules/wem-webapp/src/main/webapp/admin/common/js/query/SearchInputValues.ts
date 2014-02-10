module api.query {

    export class SearchInputValues {

        textSearchFieldValue: string;

        aggregationSelections: api.aggregation.AggregationSelection[];

        public setAggregationSelections(aggregationSelections: api.aggregation.AggregationSelection[]): void {
            this.aggregationSelections = aggregationSelections;
        }

        public setTextSearchFieldValue(textSearchFieldValue: string): void {
            this.textSearchFieldValue = textSearchFieldValue;
        }

        public getTextSearchFieldValue(): string {

            return this.textSearchFieldValue;
        }

        public getSelectedValuesForAggregationName(name: string): string[] {

            for (var i = 0; i < this.aggregationSelections.length; i++) {

                if (this.aggregationSelections[i].getName() == name) {
                    return this.aggregationSelections[i].getValues();
                }

            }

            return null;
        }
    }

}