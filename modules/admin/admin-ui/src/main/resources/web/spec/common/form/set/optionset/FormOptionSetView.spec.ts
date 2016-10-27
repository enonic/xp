module FormOptionSetViewSpec {

    import FormOptionSet = api.form.FormOptionSet;
    import FormOptionSetJson = api.form.json.FormOptionSetJson;
    import FormOptionSetOptionJson = api.form.json.FormOptionSetOptionJson;

    describe("api.form.set.optionset.FormOptionSetView", function () {

        let optionSet: FormOptionSet;

        beforeEach(function () {
            optionSet = FormOptionSetSpec.createOptionSet(FormOptionSetSpec.getOptionSetJson());
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
}