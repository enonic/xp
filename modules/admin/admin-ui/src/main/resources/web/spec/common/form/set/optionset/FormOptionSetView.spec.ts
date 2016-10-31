module FormOptionSetViewSpec {

    import FormOptionSet = api.form.FormOptionSet;
    import FormOptionSetJson = api.form.json.FormOptionSetJson;
    import FormOptionSetOptionJson = api.form.json.FormOptionSetOptionJson;
    import FormOptionSetView = api.form.FormOptionSetView;
    import FormOptionSetViewConfig = api.form.FormOptionSetViewConfig;
    import PropertySet = api.data.PropertySet;
    import FormContext = api.form.FormContext;

    describe("api.form.FormOptionSetView", function () {

        let optionSet: FormOptionSet;
        let optionSetView: FormOptionSetView;

        beforeEach(function () {
            optionSet = FormOptionSetSpec.createOptionSet(FormOptionSetSpec.getOptionSetJson());

            optionSetView = createOptionSetView(optionSet, getPropertySet());
        });

        describe("constructor", function () {

            it("should correctly initialize label", function () {
                expect(optionSet.getLabel()).toEqual("Custom Option Set");
            });

            it("should correctly initialize help text", function () {
                expect(optionSet.getHelpText()).toEqual("Custom Help Text");
            });

            it("should correctly initialize expanded property", function () {
                expect(optionSet.isExpanded()).toBeTruthy();
            });

            it("should correctly initialize occurrences config", function () {
                expect(optionSet.getOccurrences().getMinimum()).toEqual(5);
                expect(optionSet.getOccurrences().getMaximum()).toEqual(7);
            });

            it("should correctly initialize multiselection config", function () {
                expect(optionSet.getMultiselection().getMinimum()).toEqual(1);
                expect(optionSet.getMultiselection().getMaximum()).toEqual(3);
            });
        });
    });

    export function createOptionSetView(optionSet: FormOptionSet, dataSet: PropertySet): FormOptionSetView {
        return new FormOptionSetView(getFormOptionSetViewConfig(optionSet, dataSet));
    }

    export function getFormOptionSetViewConfig(optionSet: FormOptionSet, dataSet: PropertySet): FormOptionSetViewConfig {
        return {
            context: getFormContext(),
            formOptionSet: optionSet,
            parent: undefined,
            parentDataSet: dataSet
        }
    }

    export function getFormContext(): FormContext {
        return FormContext.create().setShowEmptyFormItemSetOccurrences(true).build();
    }

    export function getPropertySet(): PropertySet {
        var tree = new api.data.PropertyTree();
        var set = tree.addPropertySet('optionSet1');

        var optionSet1 = set.addPropertySet("option1");
        optionSet1.addString("input1", "Option 1 value from data");

        var optionSet2 = set.addPropertySet("option2");
        var itemSet1 = optionSet2.addPropertySet('itemSet1');
        itemSet1.addString("input2-1", "Option 2 value from data");
        itemSet1.addBoolean("input2-2", true);

        return tree.getRoot();
    }
}