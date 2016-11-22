import ComboBox = api.ui.selector.combobox.ComboBox;
import ComboBoxConfig = api.ui.selector.combobox.ComboBoxConfig;
import BaseSelectedOptionsView = api.ui.selector.combobox.BaseSelectedOptionsView;
import Viewer = api.ui.Viewer;
import hasElement = FormOptionSetSpec.hasElement;
import hasElementByClassName = FormOptionSetSpec.hasElementByClassName;
import NamesAndIconViewer = api.ui.NamesAndIconViewer;
import Option = api.ui.selector.Option;
import PositionType = api.ui.selector.combobox.PositionType;
import DropdownPosition = api.ui.selector.combobox.DropdownPosition;

interface OptionWithId<T> extends Option<T> {
    id: string;
}

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

    describe("Dropdown", () => {

        it("should show", () => {
            combobox.showDropdown();

            expect(combobox.getEl().hasClass("expanded")).toBeTruthy();
        });

        it("should hide", () => {
            combobox.hideDropdown();

            expect(combobox.getEl().hasClass("expanded")).toBeFalsy();
        });

        describe("doUpdateDropdownTopPositionAndWidth()", function () {

            it("should be displayed below (enough space)", () => {
                spyOn(combobox, "dropdownOverflowsBottom").and.callFake(function () {
                    return <DropdownPosition>{position: PositionType.BELOW, height: 500};
                });
                combobox.showDropdown();

                const dropdown = combobox.getComboBoxDropdownGrid().getElement().getEl();

                expect(dropdown.hasClass("reverted")).toBeFalsy();
                expect(dropdown.getHeight()).toEqual(200);

                combobox.hideDropdown();
            });

            it("should be displayed above (enough space)", () => {
                spyOn(combobox, "dropdownOverflowsBottom").and.callFake(function () {
                    return <DropdownPosition>{position: PositionType.ABOVE, height: 500};
                });
                combobox.showDropdown();

                const dropdown = combobox.getComboBoxDropdownGrid().getElement().getEl();

                expect(dropdown.hasClass("reverted")).toBeTruthy();
                expect(dropdown.getHeight()).toEqual(200);

                combobox.hideDropdown();
            });

            it("should be displayed below (not enough space)", () => {
                spyOn(combobox, "dropdownOverflowsBottom").and.callFake(function () {
                    return <DropdownPosition>{position: PositionType.FLEXIBLE_BELOW, height: 100};
                });
                combobox.showDropdown();

                spyOn(combobox.getComboBoxDropdownGrid(), "getOptionCount").and.returnValue(10);
                const dropdown = combobox.getComboBoxDropdownGrid().getElement().getEl();

                expect(dropdown.hasClass("reverted")).toBeFalsy();
                expect(dropdown.getHeight()).toEqual(100);

                combobox.hideDropdown();
            });

            it("should be displayed above (not enough space)", () => {
                spyOn(combobox, "dropdownOverflowsBottom").and.callFake(function () {
                    return <DropdownPosition>{position: PositionType.FLEXIBLE_ABOVE, height: 100};
                });
                combobox.showDropdown();

                spyOn(combobox.getComboBoxDropdownGrid(), "getOptionCount").and.returnValue(10);
                const dropdown = combobox.getComboBoxDropdownGrid().getElement().getEl();

                expect(dropdown.hasClass("reverted")).toBeTruthy();
                expect(dropdown.getHeight()).toEqual(100);

                combobox.hideDropdown();
            });
        });

    });

    function createComboBox(config: ComboBoxConfig<any>) {
        return new ComboBox("comboboxName", config);
    }

    function createDefaultComboBox() {
        const cbox = new ComboBox("comboboxName", createComboBoxConfig());

        createOptions(10).forEach((option) => cbox.addOption(option));

        return cbox;
    }

    function createCustomComboBox(config: Object) {
        const defaultConfig = createComboBoxConfig();
        const customConfig = (<any>Object).assign(defaultConfig, config);
        const cbox = new ComboBox("comboboxName", customConfig);

        createOptions(10).forEach((option) => cbox.addOption(option));

        return cbox;
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

    function createOption(): OptionWithId<any> {
        return <OptionWithId<any>> {
            id: "id" + Math.random().toString(36).slice(2),
            value: "value" + Math.random().toString(36).slice(2),
            displayValue: "displayValue" + Math.random().toString(36).slice(2),
        };
    }

    function createOptions(count: number = 1): OptionWithId<any>[] {
        // Array.prototype.fill hack for ES5
        return (new Array(count)).join(',').split(',').map(() => createOption());
    }
});