module api.form {

    export class FormItemOccurrence<V extends FormItemOccurrenceView> {

        private occurrences: FormItemOccurrences<V>;

        private formItem: FormItem;

        private allowedOccurrences: Occurrences;

        private index: number;

        constructor(occurrences: FormItemOccurrences<V>, index: number, allowedOccurrences: Occurrences) {
            this.occurrences = occurrences;
            this.formItem = occurrences.getFormItem();
            this.allowedOccurrences = allowedOccurrences;
            this.index = index;
        }

        setIndex(value: number) {
            this.index = value;
        }

        getIndex(): number {
            return this.index;
        }

        showRemoveButton(): boolean {
            return this.moreThanRequiredOccurrences();
        }

        showAddButton(): boolean {

            if (!this.isLastOccurrence()) {
                return false;
            }

            return this.lessOccurrencesThanMaximumAllowed();
        }

        public isMultiple(): boolean {
            return this.allowedOccurrences.multiple();
        }

        public oneAndOnly() {
            return this.index == 0 && this.occurrences.countOccurrences() == 1;
        }

        private moreThanRequiredOccurrences() {
            return this.occurrences.countOccurrences() > this.allowedOccurrences.getMinimum();
        }

        private lessOccurrencesThanMaximumAllowed(): boolean {
            return !this.allowedOccurrences.maximumReached(this.occurrences.countOccurrences());
        }

        private isLastOccurrence() {
            return this.index == this.occurrences.countOccurrences() - 1;
        }
    }
}