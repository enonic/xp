describe("api.ui.ActionTest", function () {

    it("test getLabel", function () {
        var action = new api.ui.Action('My Action');
        expect(action.getLabel()).toBe('My Action');
    });

    it("test addPropertyChangeListener is invoked on enabled", function () {
        var action = new api.ui.Action('My Action');

        action.setEnabled(true);
        expect(action.isEnabled()).toBe(true);

        action.onPropertyChanged(function (action) {
            expect(action.isEnabled()).toBe(false);
        });

        action.setEnabled(false);
    });

    it("test addPropertyChangeListener is invoked on label", function () {
        var action = new api.ui.Action('My Action');

        action.onPropertyChanged(function (action) {
            expect(action.getLabel()).toBe('Changed label');
        });

        action.setLabel('Changed label');
    });

});
