module app_wizard_form {

    export class FormItemOccurrence {

        private occurrences:FormItemOccurrences;

        private formItem:api_form.FormItem;

        private allowedOccurrences:api_form.Occurrences;

        private index:number;

        constructor(occurrences:FormItemOccurrences, index:number, allowedOccurrences:api_form.Occurrences) {
            this.occurrences = occurrences;
            this.formItem = occurrences.getFormItem();
            this.allowedOccurrences = allowedOccurrences;
            this.index = index;
        }

        setIndex(value:number) {
            this.index = value;
        }

        getIndex():number {
            return this.index;
        }

        getDataId():api_data.DataId {
            return new api_data.DataId(this.formItem.getName(), this.index);
        }

        showRemoveButton():boolean {

            if (this.oneAndOnly()) {
                return false;
            }
            else {
                return this.moreThanRequiredOccurrences();
            }
        }

        showAddButton():boolean {

            if (!this.isLastOccurrence()) {
                return false;
            }

            return this.lessOccurrencesThanMaximumAllowed();
        }

        private oneAndOnly() {
            return this.index == 0 && this.occurrences.countOccurrences() == 1;
        }

        private moreThanRequiredOccurrences() {
            return this.allowedOccurrences.minimumReached(this.occurrences.countOccurrences());
        }

        private lessOccurrencesThanMaximumAllowed():boolean {
            return !this.allowedOccurrences.maximumReached(this.occurrences.countOccurrences());
        }

        private isLastOccurrence() {
            return this.index == this.occurrences.countOccurrences() - 1;
        }
    }
}