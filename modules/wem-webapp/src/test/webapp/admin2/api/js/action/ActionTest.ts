///<reference path='../TestCase.d.ts' />
///<reference path='../../../../../../main/webapp/admin2/api/js/ui/Action.ts' />

TestCase("Action", {

    "test getLabel": function () {

        assertEquals("My action", new api_ui.Action('My action').getLabel());
    },
    "test given setEnabled invoked then addPropertyChangeListener is invoked and action isEnabled is correct": function () {

        var action = new api_ui.Action('My action');
        action.setEnabled(true);
        assertEquals(true, action.isEnabled());
        action.addPropertyChangeListener((action:api_ui.Action) => {
            assertEquals(false, action.isEnabled());
        });
        action.setEnabled(false);
    },
    "test given setLabel invoked then addPropertyChangeListener is invoked and action getLabel is correct": function () {

        var action = new api_ui.Action('My action');
        action.addPropertyChangeListener((action:api_ui.Action) => {
            assertEquals("Changed label", action.getLabel());
        });
        action.setLabel("Changed label");
    }

});

