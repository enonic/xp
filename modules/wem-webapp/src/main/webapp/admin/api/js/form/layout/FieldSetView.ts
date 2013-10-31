module api_form_layout {

    export class FieldSetView extends LayoutView {

        private fieldSet:api_form.FieldSet;

        private formItemViews:api_form.FormItemView[] = [];

        constructor(fieldSet:api_form.FieldSet) {
            super(fieldSet, "FieldSetView", "field-set-view");

            this.fieldSet = fieldSet;
            this.doLayout();
        }

        getData():api_data.Data[] {
            return null;
        }

        getFormItemViews():api_form.FormItemView[] {
            return this.formItemViews;
        }

        private doLayout() {

            var label = new FieldSetLabel(this.fieldSet);
            this.appendChild(label);

            var wrappingDiv = new api_dom.DivEl(null, "field-set-container");
            this.appendChild(wrappingDiv);

            this.fieldSet.getFormItems().forEach((formItem:api_form.FormItem) => {
                if (formItem instanceof api_form.FormItemSet) {
                    var formItemSet:api_form.FormItemSet = <api_form.FormItemSet>formItem;
                    console.log("FieldSetView.doLayout() laying out FormItemSet: ", formItemSet);
                    var formItemSetView = new api_form_formitemset.FormItemSetView(formItemSet);
                    wrappingDiv.appendChild(formItemSetView);
                    this.formItemViews.push(formItemSetView);
                }
                else if (formItem instanceof api_form.Input) {
                    var input:api_form.Input = <api_form.Input>formItem;
                    console.log("FieldSetView.doLayout()  laying out Input: ", input);
                    var inputContainerView = new api_form_input.InputView(input);
                    wrappingDiv.appendChild(inputContainerView);
                    this.formItemViews.push(inputContainerView);
                }
            });

        }
    }
}