import ComboBox = api.ui.selector.combobox.ComboBox;
import Spy = jasmine.Spy;
import ComboBoxConfig = api.ui.selector.combobox.ComboBoxConfig;
import BaseSelectedOptionsView = api.ui.selector.combobox.BaseSelectedOptionsView;
import Viewer = api.ui.Viewer;
import hasElement = FormOptionSetSpec.hasElement;
import hasElementByClassName = FormOptionSetSpec.hasElementByClassName;
import NamesAndIconViewer = api.ui.NamesAndIconViewer;

describe("api.ui.selector.combobox.ComboBox", () => {

    let combobox: ComboBox<any>;

    beforeEach(() => {
        combobox = createDefaultComboBox();
    });

    describe("constructor", () => {

        it("should correctly set name", () => {
            const name = combobox.getEl().getAttribute("name");
            expect(name).toEqual("comboboxName");
        });

        describe("should correctly initialize apply button", function () {

            it("`apply` button is present", () => {
                expect(hasElement(combobox, ".apply-button")).toBeTruthy();
            });

            it("no `apply` button for maximum occurrences of 1", () => {
                const cbox = createCustomComboBox({maximumOccurrences: 1});
                expect(hasElement(cbox, ".apply-button")).toBeFalsy();
            });

            it("no `apply` button without selected options view", () => {
                const cbox = createCustomComboBox(<ComboBoxConfig<any>>{selectedOptionsView: null});
                expect(hasElement(cbox, ".apply-button")).toBeFalsy();
            });

        });
    });

    describe("dropdown", () => {

        it("should show", () => {
            combobox.showDropdown();

            expect(combobox.getEl().hasClass("expanded")).toBeTruthy();
            expect(combobox.isDropdownShown()).toBeTruthy();
        });

        it("should hide", () => {
            combobox.hideDropdown();

            expect(combobox.getEl().hasClass("expanded")).toBeFalsy();
            expect(combobox.isDropdownShown()).toBeFalsy();
        });

    });

    function createComboBox(config: ComboBoxConfig<any>) {
        return new ComboBox("comboboxName", config);
    }

    function createDefaultComboBox() {
        return new ComboBox("comboboxName", createComboBoxConfig());
    }

    function createCustomComboBox(config: Object) {
        const defaultConfig = createComboBoxConfig();
        const customConfig = (<any>Object).assign(defaultConfig, config);
        return new ComboBox("comboboxName", customConfig);
    }

    function createComboBoxConfig(): ComboBoxConfig<any> {
        const optionDisplayValueViewer = new NamesAndIconViewer();
        const selectedOptionsView = new BaseSelectedOptionsView();

        return <ComboBoxConfig<any>> {
            iconUrl: "https://example.com/image.png",

            optionDisplayValueViewer,

            selectedOptionsView,

            maximumOccurrences: 3,

            // filter: function filter() { return true; },

            hideComboBoxWhenMaxReached: true,

            setNextInputFocusWhenMaxReached: true,

            dataIdProperty: "id",

            delayedInputValueChangedHandling: 100,

            minWidth: 100,

            maxHeight: 200,

            value: "value",

            noOptionsText: "No options",

            displayMissingSelectedOptions: true,

            removeMissingSelectedOptions: true,

            skipAutoDropShowOnValueChange: true
        };
    }

    function createComboBoxMinimumConfig(): ComboBoxConfig<any> {
        const selectedOptionsView = new BaseSelectedOptionsView();
        return <ComboBoxConfig<any>> {
            selectedOptionsView
        };
    }
});