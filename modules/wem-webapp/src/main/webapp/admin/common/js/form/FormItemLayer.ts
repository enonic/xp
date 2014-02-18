module api.form{

    export class FormItemLayer {

        private context:FormContext;

        private formItems:api.form.FormItem[];

        private parentEl:api.dom.Element;

        private formItemViews:FormItemView[] = [];

        private parent:api.form.formitemset.FormItemSetOccurrenceView;

        setFormContext(context:FormContext):FormItemLayer {
            this.context = context;
            return this;
        }

        setFormItems(formItems:api.form.FormItem[]):FormItemLayer {
            this.formItems = formItems;
            return this;
        }

        setParentElement(parentEl:api.dom.Element):FormItemLayer {
            this.parentEl = parentEl;
            return this;
        }

        setParent(value:api.form.formitemset.FormItemSetOccurrenceView):FormItemLayer {
            this.parent = value;
            return this;
        }

        layout(dataSet:api.data.DataSet):FormItemView[] {
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
                    var fieldSet:FieldSet = <api.form.FieldSet>formItem;
                    var fieldSetView = new api.form.layout.FieldSetView(this.context, fieldSet, this.parent);

                    this.parentEl.appendChild(fieldSetView);
                    this.formItemViews.push(fieldSetView);
                }
                else if (formItem instanceof FormItemSet) {
                    var formItemSet:FormItemSet = <FormItemSet>formItem;
                    var formItemSetView = new api.form.formitemset.FormItemSetView(this.context, formItemSet, this.parent);

                    this.parentEl.appendChild(formItemSetView);
                    this.formItemViews.push(formItemSetView);
                }
                else if (formItem instanceof Input) {
                    var input:Input = <Input>formItem;
                    var inputView = new api.form.input.InputView(this.context, input, this.parent);

                    this.parentEl.appendChild(inputView);
                    this.formItemViews.push(inputView);
                }
            });
        }

        private doLayoutWithData(dataSet:api.data.DataSet) {

            this.formItems.forEach((formItem:FormItem) => {
                if (formItem instanceof FormItemSet) {

                    var formItemSet:FormItemSet = <FormItemSet>formItem;
                    var dataSets:api.data.DataSet[] = dataSet.getDataSetsByName(formItemSet.getName());
                    var formItemSetView = new api.form.formitemset.FormItemSetView(this.context, formItemSet, this.parent, dataSets);

                    this.parentEl.appendChild(formItemSetView);
                    this.formItemViews.push(formItemSetView);
                }
                else if (formItem instanceof FieldSet) {

                    var fieldSet:FieldSet = <FieldSet>formItem;
                    var fieldSetView = new api.form.layout.FieldSetView(this.context, fieldSet, this.parent, dataSet);

                    this.parentEl.appendChild(fieldSetView);
                    this.formItemViews.push(fieldSetView);
                }
                else if (formItem instanceof Input) {

                    var input:Input = <Input>formItem;
                    var properties:api.data.Property[] = dataSet.getPropertiesByName(input.getName());
                    var inputView = new api.form.input.InputView(this.context, input, this.parent, properties);

                    this.parentEl.appendChild(inputView);
                    this.formItemViews.push(inputView);
                }
            });
        }
    }
}