module api.form {

    export class FormOptionSet extends FormSet implements FormItemContainer {

        private options: FormOptionSetOption[] = [];

        private expanded: boolean;

        private multiselection: Occurrences;

        constructor(formOptionSetJson: api.form.json.FormOptionSetJson) {
            super(formOptionSetJson);
            this.expanded = formOptionSetJson.expanded;
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

        isExpanded(): boolean {
            return this.expanded;
        }

        getMultiselection(): Occurrences {
            return this.multiselection;
        }

        isRadioSelection(): boolean {
            return this.multiselection.getMinimum() == 1 && this.multiselection.getMaximum() == 1
        }

        public toFormOptionSetJson(): api.form.json.FormItemTypeWrapperJson {

            return <api.form.json.FormItemTypeWrapperJson>{
                FormOptionSet: <api.form.json.FormOptionSetJson>{
                    name: this.getName(),
                    expanded: this.isExpanded(),
                    options: FormOptionSetOption.optionsToJson(this.getOptions()),
                    label: this.getLabel(),
                    helpText: this.getHelpText(),
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

            if (!api.ObjectHelper.booleanEquals(this.expanded, other.expanded)) {
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