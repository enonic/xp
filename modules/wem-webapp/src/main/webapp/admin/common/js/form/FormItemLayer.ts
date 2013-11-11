module api_form{

    export class FormItemLayer {

        private formItems:api_form.FormItem[];

        private parentEl:api_dom.Element;

        private formItemViews:FormItemView[] = [];

        setFormItems(formItems:api_form.FormItem[]):FormItemLayer {
            this.formItems = formItems;
            return this;
        }

        setParentElement(parentEl:api_dom.Element):FormItemLayer {
            this.parentEl = parentEl;
            return this;
        }

        layout(dataSet:api_data.DataSet):FormItemView[] {
            this.formItemViews = [];
            if( dataSet == null ) {
                this.doLayoutWithoutData()
            }
            else {
                this.doLayoutWithData(dataSet);
            }

            return this.formItemViews;
        }

        private doLayoutWithoutData() {

            this.formItems.forEach((formItem:FormItem) => {
                if (formItem instanceof FieldSet) {
                    var fieldSet:FieldSet = <api_form.FieldSet>formItem;
                    var fieldSetView = new api_form_layout.FieldSetView(fieldSet);

                    this.parentEl.appendChild(fieldSetView);
                    this.formItemViews.push(fieldSetView);
                }
                else if (formItem instanceof FormItemSet) {
                    var formItemSet:FormItemSet = <FormItemSet>formItem;
                    var formItemSetView = new api_form_formitemset.FormItemSetView(formItemSet);

                    this.parentEl.appendChild(formItemSetView);
                    this.formItemViews.push(formItemSetView);
                }
                else if (formItem instanceof Input) {
                    var input:Input = <Input>formItem;
                    var inputView = new api_form_input.InputView(input);

                    this.parentEl.appendChild(inputView);
                    this.formItemViews.push(inputView);
                }
            });
        }

        private doLayoutWithData(dataSet:api_data.DataSet) {

            this.formItems.forEach((formItem:FormItem) => {
                if (formItem instanceof FormItemSet) {

                    var formItemSet:FormItemSet = <FormItemSet>formItem;
                    var dataSets:api_data.DataSet[] = dataSet.getDataSetsByName(formItemSet.getName());
                    var formItemSetView = new api_form_formitemset.FormItemSetView(formItemSet, dataSets);

                    this.parentEl.appendChild(formItemSetView);
                    this.formItemViews.push(formItemSetView);
                }
                else if (formItem instanceof FieldSet) {

                    var fieldSet:FieldSet = <FieldSet>formItem;
                    var fieldSetView = new api_form_layout.FieldSetView(fieldSet, dataSet);

                    this.parentEl.appendChild(fieldSetView);
                    this.formItemViews.push(fieldSetView);
                }
                else if (formItem instanceof Input) {

                    var input:Input = <Input>formItem;
                    var properties:api_data.Property[] = dataSet.getPropertiesByName(input.getName());
                    var inputView = new api_form_input.InputView(input, properties);

                    this.parentEl.appendChild(inputView);
                    this.formItemViews.push(inputView);
                }
            });
        }
    }
}