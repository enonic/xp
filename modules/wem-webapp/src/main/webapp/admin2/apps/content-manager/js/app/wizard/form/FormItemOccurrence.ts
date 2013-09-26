module app_wizard_form {

    export class FormItemOccurrence {

        private occurrences:FormItemOccurrences;

        private formItem:api_schema_content_form.FormItem;

        private allowedOccurrences:api_schema_content_form.Occurrences;

        private index:number;

        constructor(occurrences:FormItemOccurrences, index:number, allowedOccurrences:api_schema_content_form.Occurrences) {
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
            return this.occurrences.countOccurrences() > this.allowedOccurrences.getMinimum();
        }

        private lessOccurrencesThanMaximumAllowed():boolean {

            if (this.allowedOccurrences.getMaximum() == 0) {
                return true;
            }
            return this.occurrences.countOccurrences() < this.allowedOccurrences.getMaximum();
        }

        private isLastOccurrence() {
            return this.index == this.occurrences.countOccurrences() - 1;
        }
    }
}