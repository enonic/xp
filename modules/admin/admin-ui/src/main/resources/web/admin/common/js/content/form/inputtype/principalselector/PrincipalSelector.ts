module api.content.form.inputtype.principalselector {

    import Property = api.data.Property;
    import PropertyArray = api.data.PropertyArray;
    import Value = api.data.Value;
    import ValueType = api.data.ValueType;
    import ValueTypes = api.data.ValueTypes;
    import GetRelationshipTypeByNameRequest = api.schema.relationshiptype.GetRelationshipTypeByNameRequest;
    import RelationshipTypeName = api.schema.relationshiptype.RelationshipTypeName;
    import SelectedOptionEvent = api.ui.selector.combobox.SelectedOptionEvent;
    import FocusSwitchEvent = api.ui.FocusSwitchEvent;
    import PrincipalSelectedOptionView = api.ui.security.PrincipalSelectedOptionView;

    export class PrincipalSelector extends api.form.inputtype.support.BaseInputTypeManagingAdd<api.security.Principal> {

        private config: api.content.form.inputtype.ContentInputTypeViewContext;

        private principalTypes: api.security.PrincipalType[];

        private comboBox: api.ui.security.PrincipalComboBox;

        constructor(config?: api.content.form.inputtype.ContentInputTypeViewContext) {
            super('relationship');
            this.addClass('input-type-view');
            this.config = config;
            this.readConfig(config.inputConfig);
        }

        private readConfig(inputConfig: { [element: string]: { [name: string]: string }[]; }): void {
            let principalTypeConfig = inputConfig['principalType'] || [];
            this.principalTypes =
                principalTypeConfig.map((cfg) => cfg['value']).filter((val) => !!val).map(
                    (val: string) => api.security.PrincipalType[val]).filter((val) => !!val);
        }

        public getPrincipalComboBox(): api.ui.security.PrincipalComboBox {
            return this.comboBox;
        }

        getValueType(): ValueType {
            return ValueTypes.REFERENCE;
        }

        newInitialValue(): Value {
            return null;
        }

        layout(input: api.form.Input, propertyArray: PropertyArray): wemQ.Promise<void> {

            super.layout(input, propertyArray);
            this.comboBox = this.createComboBox(input);

            this.appendChild(this.comboBox);

            this.setLayoutInProgress(false);

            return wemQ<void>(null);
        }

        update(propertyArray: api.data.PropertyArray, unchangedOnly?: boolean): Q.Promise<void> {
            let superPromise = super.update(propertyArray, unchangedOnly);

            if (!unchangedOnly || !this.comboBox.isDirty()) {
                return superPromise.then(() => {
                    this.comboBox.setValue(this.getValueFromPropertyArray(propertyArray));
                });
            } else {
                return superPromise;
            }
        }

        reset() {
            this.comboBox.resetBaseValues();
        }

        private createComboBox(input: api.form.Input): api.ui.security.PrincipalComboBox {

            let value = this.getValueFromPropertyArray(this.getPropertyArray());
            let principalLoader = new api.security.PrincipalLoader().setAllowedTypes(this.principalTypes);

            let comboBox = api.ui.security.PrincipalComboBox.create().setLoader(principalLoader).setMaxOccurences(
                input.getOccurrences().getMaximum()).setValue(value).build();

            comboBox.onOptionDeselected((event: SelectedOptionEvent<api.security.Principal>) => {
                this.getPropertyArray().remove(event.getSelectedOption().getIndex());
                this.validate(false);
            });

            comboBox.onOptionSelected((event: SelectedOptionEvent<api.security.Principal>) => {
                this.fireFocusSwitchEvent(event);

                const selectedOption = event.getSelectedOption();
                let key = selectedOption.getOption().displayValue.getKey();
                if (!key) {
                    return;
                }
                let selectedOptionView: PrincipalSelectedOptionView = <PrincipalSelectedOptionView>selectedOption.getOptionView();
                this.saveToSet(selectedOptionView.getOption(), selectedOption.getIndex());
                this.validate(false);
            });

            comboBox.onOptionMoved((selectedOption: api.ui.selector.combobox.SelectedOption<api.security.Principal>) => {
                let selectedOptionView: PrincipalSelectedOptionView = <PrincipalSelectedOptionView> selectedOption.getOptionView();
                this.saveToSet(selectedOptionView.getOption(), selectedOption.getIndex());
                this.validate(false);
            });

            return comboBox;
        }

        private saveToSet(principalOption: api.ui.selector.Option<api.security.Principal>, index: number) {
            this.getPropertyArray().set(index, ValueTypes.REFERENCE.newValue(principalOption.value));
        }

        private refreshSortable() {
            wemjq(this.getHTMLElement()).find('.selected-options').sortable('refresh');
        }

        protected getNumberOfValids(): number {
            return this.getPropertyArray().getSize();
        }

        giveFocus(): boolean {
            if (this.comboBox.maximumOccurrencesReached()) {
                return false;
            }
            return this.comboBox.giveFocus();
        }

        onFocus(listener: (event: FocusEvent) => void) {
            this.comboBox.onFocus(listener);
        }

        unFocus(listener: (event: FocusEvent) => void) {
            this.comboBox.unFocus(listener);
        }

        onBlur(listener: (event: FocusEvent) => void) {
            this.comboBox.onBlur(listener);
        }

        unBlur(listener: (event: FocusEvent) => void) {
            this.comboBox.unBlur(listener);
        }

    }

    api.form.inputtype.InputTypeManager.register(new api.Class('PrincipalSelector', PrincipalSelector));
}
