module api.content.form.inputtype.customselector {

    import PropertyArray = api.data.PropertyArray;
    import Property = api.data.Property;
    import Value = api.data.Value;
    import ValueType = api.data.ValueType;
    import ValueTypes = api.data.ValueTypes;
    import SelectedOption = api.ui.selector.combobox.SelectedOption;
    import OptionSelectedEvent = api.ui.selector.OptionSelectedEvent;
    import SelectedOptionEvent = api.ui.selector.combobox.SelectedOptionEvent;
    import FocusSwitchEvent = api.ui.FocusSwitchEvent;
    import ComboBoxOption = api.form.inputtype.combobox.ComboBoxOption;
    import ComboBoxDisplayValueViewer = api.form.inputtype.combobox.ComboBoxDisplayValueViewer;
    import Dropdown = api.ui.selector.dropdown.Dropdown;
    import Viewer = api.ui.Viewer;
    import NamesAndIconViewer = api.ui.NamesAndIconViewer;
    import JsonRequest = api.rest.JsonRequest;
    import StringHelper = api.util.StringHelper;
    import UriHelper = api.util.UriHelper;
    import Path = api.rest.Path;
    import JsonResponse = api.rest.JsonResponse;
    import ContentInputTypeViewContext = api.content.form.inputtype.ContentInputTypeViewContext;
    import ElementBuilder = api.dom.ElementBuilder;
    import NewElementBuilder = api.dom.NewElementBuilder;
    import RichComboBox = api.ui.selector.combobox.RichComboBox;

    export class CustomSelector extends api.form.inputtype.support.BaseInputTypeManagingAdd<CustomSelectorItem> {

        public static debug: boolean = false;

        private static portalUrl: string = UriHelper.getPortalUri('/edit/draft{0}/_/service/{1}');

        private requestPath: string;

        private context: ContentInputTypeViewContext;

        private comboBox: RichComboBox<CustomSelectorItem>;

        private draggingIndex: number;

        constructor(context: api.content.form.inputtype.ContentInputTypeViewContext) {
            super('custom-selector');

            if (CustomSelector.debug) {
                console.debug("CustomSelector: config", context.inputConfig);
            }

            this.context = context;
            this.readConfig(context);
        }

        private readConfig(context: ContentInputTypeViewContext): void {
            let serviceUrl = context.inputConfig['service'][0]['value'];
            let serviceParams = context.inputConfig['param'] || [];
            let contentPath = context.contentPath.toString();

            let params = serviceParams.reduce((prev, curr) => {
                prev[curr['@value']] = curr['value'];
                return prev;
            }, {});

            this.requestPath = StringHelper.format(CustomSelector.portalUrl, contentPath, UriHelper.appendUrlParams(serviceUrl, params));
        }

        getValueType(): ValueType {
            return ValueTypes.STRING;
        }

        newInitialValue(): Value {
            return ValueTypes.STRING.newNullValue();
        }

        layout(input: api.form.Input, propertyArray: PropertyArray): wemQ.Promise<void> {
            if (!ValueTypes.STRING.equals(propertyArray.getType())) {
                propertyArray.convertValues(ValueTypes.STRING);
            }
            super.layout(input, propertyArray);

            this.comboBox = this.createComboBox(input, propertyArray);

            this.appendChild(this.comboBox);

            this.setupSortable();
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

        createComboBox(input: api.form.Input, propertyArray: PropertyArray): RichComboBox<CustomSelectorItem> {

            let comboBox = new CustomSelectorComboBox(input, this.requestPath, this.getValueFromPropertyArray(propertyArray));
            /*
             comboBox.onOptionFilterInputValueChanged((event: api.ui.selector.OptionFilterInputValueChangedEvent<string>) => {
             comboBox.setFilterArgs({searchString: event.getNewValue()});
             });
             */
            comboBox.onOptionSelected((event: SelectedOptionEvent<CustomSelectorItem>) => {
                this.ignorePropertyChange = true;

                const option = event.getSelectedOption();
                let value = new Value(String(option.getOption().value), ValueTypes.STRING);
                if (option.getIndex() >= 0) {
                    this.getPropertyArray().set(option.getIndex(), value);
                } else {
                    this.getPropertyArray().add(value);
                }
                this.refreshSortable();

                this.ignorePropertyChange = false;
                this.validate(false);

                this.fireFocusSwitchEvent(event);
            });
            comboBox.onOptionDeselected((event: SelectedOptionEvent<CustomSelectorItem>) => {
                this.ignorePropertyChange = true;

                this.getPropertyArray().remove(event.getSelectedOption().getIndex());

                this.refreshSortable();
                this.ignorePropertyChange = false;
                this.validate(false);
            });

            comboBox.onValueLoaded((options) => {
                this.validate(false);
            });

            return comboBox;
        }

        protected getNumberOfValids(): number {
            return this.comboBox.countSelected();
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

        private setupSortable() {
            this.updateSelectedOptionStyle();
            wemjq(this.getHTMLElement()).find(".selected-options").sortable({
                axis: "y",
                containment: 'parent',
                handle: '.drag-control',
                tolerance: 'pointer',
                start: (event: Event, ui: JQueryUI.SortableUIParams) => this.handleDnDStart(event, ui),
                update: (event: Event, ui: JQueryUI.SortableUIParams) => this.handleDnDUpdate(event, ui)
            });
        }

        private refreshSortable() {
            this.updateSelectedOptionStyle();
            wemjq(this.getHTMLElement()).find(".selected-options").sortable("refresh");
        }

        private handleDnDStart(event: Event, ui: JQueryUI.SortableUIParams): void {

            let draggedElement = api.dom.Element.fromHtmlElement(<HTMLElement>ui.item.context);
            this.draggingIndex = draggedElement.getSiblingIndex();

            ui.placeholder.html("Drop form item set here");
        }

        private handleDnDUpdate(event: Event, ui: JQueryUI.SortableUIParams) {

            if (this.draggingIndex >= 0) {
                let draggedElement = api.dom.Element.fromHtmlElement(<HTMLElement>ui.item.context);
                let draggedToIndex = draggedElement.getSiblingIndex();
                this.getPropertyArray().move(this.draggingIndex, draggedToIndex);
            }

            this.draggingIndex = -1;
        }

        private updateSelectedOptionStyle() {
            if (this.getPropertyArray().getSize() > 1) {
                this.addClass("multiple-occurrence").removeClass("single-occurrence");
            } else {
                this.addClass("single-occurrence").removeClass("multiple-occurrence");
            }
        }
    }

    api.form.inputtype.InputTypeManager.register(new api.Class("CustomSelector", CustomSelector));
}
