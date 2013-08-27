module app_wizard_form {

    export class FormItemsLayer {

        private parentEl:api_dom.Element;

        constructor(parentEl:api_dom.Element) {
            this.parentEl = parentEl;
        }

        public layout(formItems:api_schema_content_form.FormItem[], parentDataSet:api_data.DataSet):FormItemContainer[] {

            console.log("FormItemsLayer.layout");
            console.log(".. formItems: ", formItems);
            console.log(".. parentDataSet: ", parentDataSet);

            var formItemContainers:FormItemContainer[] = [];

            formItems.forEach((formItem:api_schema_content_form.FormItem) => {

                if (formItem instanceof api_schema_content_form.FormItemSet) {

                    var formItemSet:api_schema_content_form.FormItemSet = <api_schema_content_form.FormItemSet>formItem;

                    console.log("laying out FormItemSet: ", formItemSet);

                    if (parentDataSet != null) {

                        var formItemSetContainer = new FormItemSetContainer(formItemSet, parentDataSet);
                        formItemContainers.push(formItemSetContainer);
                        this.parentEl.appendChild(formItemSetContainer);
                    }
                    else {

                        //var formItemSetContainer = new FormItemSetContainer(formItemSet);
                        //this.parentEl.appendChild(formItemSetContainer);
                    }
                }
                else if (formItem instanceof api_schema_content_form.Input) {

                    var input:api_schema_content_form.Input = <api_schema_content_form.Input>formItem;

                    console.log("laying out Input: ", input);

                    if (parentDataSet != null) {
                        var inputContainer = new InputContainer(input, parentDataSet);
                        formItemContainers.push(inputContainer);
                        this.parentEl.appendChild(inputContainer);
                    }
                    else {
                        //var inputContainer = new InputContainer(input);
                        //this.parentEl.appendChild(inputContainer);
                    }
                }
            });

            return formItemContainers;
        }
    }
}