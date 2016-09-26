module api.form.optionset {

    export class FormOptionSet extends FormItem implements FormItemContainer {

        private label: string;

        private options: FormOptionSetOption[] = [];

        private expanded: boolean;

        private occurrences: Occurrences;

        private multiselection: Occurrences;

        constructor(formOptionSetJson: api.form.json.FormOptionSetJson) {
            super(formOptionSetJson.name);
            this.label = formOptionSetJson.label;
            this.expanded = formOptionSetJson.expanded;
            this.occurrences = Occurrences.fromJson(formOptionSetJson.occurrences);
            this.multiselection = Occurrences.fromJson(formOptionSetJson.multiselection);

            if (formOptionSetJson.options != null) {
                formOptionSetJson.options.forEach((formOptionSetOptionJson: api.form.json.FormOptionSetOptionJson) => {
                    var option = FormOptionSetOption.fromJson(formOptionSetOptionJson);
                    if (option) {
                        this.addSetOption(option);
                    }
                });
            }
        }

        addSetOption(option: FormOptionSetOption) {
            this.options.push(option);
            option.setParent(this);
        }

        getFormItems(): FormItem[] {
            return this.options;
        }

        getOptions(): FormOptionSetOption[] {
            return this.options;
        }

        getLabel(): string {
            return this.label;
        }

        isExpanded(): boolean {
            return this.expanded;
        }

        getOccurrences(): Occurrences {
            return this.occurrences;
        }

        getMultiselection(): Occurrences {
            return this.multiselection;
        }

        public toFormOptionSetJson(): api.form.json.FormItemTypeWrapperJson {

            return <api.form.json.FormItemTypeWrapperJson>{
                FormOptionSet: <api.form.json.FormOptionSetJson>{
                    name: this.getName(),
                    expanded: this.isExpanded(),
                    options: FormOptionSetOption.optionsToJson(this.getOptions()),
                    label: this.getLabel(),
                    occurrences: this.getOccurrences().toJson(),
                    multiselection: this.getMultiselection().toJson()
                }
            };
        }

        equals(o: api.Equitable): boolean {

            if (!api.ObjectHelper.iFrameSafeInstanceOf(o, FormOptionSet)) {
                return false;
            }

            if (!super.equals(o)) {
                return false;
            }

            var other = <FormOptionSet>o;

            if (!api.ObjectHelper.stringEquals(this.label, other.label)) {
                return false;
            }

            if (!api.ObjectHelper.booleanEquals(this.expanded, other.expanded)) {
                return false;
            }

            if (!api.ObjectHelper.equals(this.occurrences, other.occurrences)) {
                return false;
            }

            if (!api.ObjectHelper.equals(this.multiselection, other.multiselection)) {
                return false;
            }

            if (!api.ObjectHelper.arrayEquals(this.options, other.options)) {
                return false;
            }

            return true;
        }
    }
}