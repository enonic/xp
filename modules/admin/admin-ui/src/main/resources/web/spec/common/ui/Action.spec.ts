import Action = api.ui.Action;

describe("api.ui.Action", () => {

    var action;

    beforeEach(() => {
        action = new Action('My Action');
    })

    it("test getLabel", () => {
        expect(action.getLabel()).toBe('My Action');
    });

    it("test addPropertyChangeListener is invoked on enabled", () => {

        action.setEnabled(true);
        expect(action.isEnabled()).toBe(true);

        action.onPropertyChanged((action) => {
            expect(action.isEnabled()).toBe(false);
        });

        action.setEnabled(false);
    });

    it("test addPropertyChangeListener is invoked on label", () => {

        action.onPropertyChanged(function (action) {
            expect(action.getLabel()).toBe('Changed label');
        });

        action.setLabel('Changed label');
    });

});
