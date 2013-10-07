describe("Action", function () {

    it("getLabel", function () {

        var action = new api_ui.Action("My action");
        expect(action.getLabel()).toBe("My action");

    });

    it("given setEnabled invoked then addPropertyChangeListener is invoked and action isEnabled is correct", function () {

        var action = new api_ui.Action('My action');

        action.setEnabled(true);
        expect(action.isEnabled()).toBe(true);

        action.addPropertyChangeListener(function () {
            expect(action.isEnabled()).toBe(false);
        });

        action.setEnabled(false);

    });

    it("given setLabel invoked then addPropertyChangeListener is invoked and action getLabel is correct", function () {

        var action = new api_ui.Action('My action');
        action.addPropertyChangeListener(function () {
            expect(action.getLabel()).toBe("Changed label");
        });

        action.setLabel("Changed label");

    });

});
