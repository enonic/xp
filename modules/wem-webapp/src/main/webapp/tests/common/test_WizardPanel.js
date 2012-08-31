function getTab(wizard, index) {
    var progress = wizard.getProgressBar();
    return progress.el.down("li[wizardStep=" + (index + 1) + "] a.step", true);
}

function getTabName(wizard, index) {
    var a = getTab(wizard, index);
    return a ? a.innerHTML : undefined;
}

function getStepNumber(wizard) {
    var at = wizard.getLayout().getActiveItem();
    return Ext.Array.indexOf(wizard.items, at);
}

function getButton(wizard, name) {
    var bbar = wizard.dockedItems.items[1];
    if (bbar) {
        return bbar.child(name);
    }
}

function getField(wizard, name) {
    return wizard.down(name);
}


function testWizard(t, wizard) {

    t.diag("Test wizard dirty and valid states, progress bar, buttons");

    var textOne = wizard.down('#textOne');
    var textTwo = wizard.down('#textTwo');
    var textThree = wizard.down('#textThree');

    t.is(wizard.items.getCount(), 3, "Wizard must have had 3 steps");
    t.is(getTabName(wizard, 0), "1. stepOne", "First tab must have had title '1. stepOne' from stepTitle attr.");
    t.is(getTabName(wizard, 1), "2. stepTwo", "Second tab must have had title '2. stepTwo' from title attr.");

    t.notOk(wizard.isWizardDirty, "Wizard must have been not dirty after start.");
    t.notOk(wizard.isWizardValid, "Wizard must have been not valid after start because mandatory field is empty.");
    t.ok(wizard.isStepValid(), "Wizard first step must have been valid after start.");

    t.chain(
        function (next) {

            t.notOk(getButton(wizard, "#next").isDisabled(), "First step next button must have been enabled because step is valid.");
            t.ok(getField(wizard, "#textOne").getActionEl().hasCls('x-form-focus'), "First field must have been focused");

            t.click(getTab(wizard, 2), next);
        },
        function (next) {

            t.is(getStepNumber(wizard), 0, "First step must still have been active because 3rd is not clickable.");

            t.waitForEvent(wizard, 'stepchanged', next);
            t.click(getTab(wizard, 1));
        },
        function (next) {

            t.is(getStepNumber(wizard), 1, "Second step must have been activated.");
            t.ok(getField(wizard, "#textThree").getActionEl().hasCls('x-form-focus'), "First field must have been focused");
            t.ok(getButton(wizard, "#next").isDisabled(), "Second step next button must have been disabled because step is not valid.");

            t.waitForEvent(wizard, 'validitychange', next);
            t.type(textThree, 'Three[ENTER]');
        },
        function (next) {

            t.ok(wizard.isWizardDirty, "Wizard must have been dirty after setting fields value.");
            t.ok(wizard.isWizardValid,
                "Wizard must have been valid after setting mandatory field, even though non-mandatory one is missing.");
            t.notOk(getButton(wizard, "#next").isDisabled(),
                "Second step next button must have been enabled after setting mandatory field.");

            t.click(getTab(wizard, 2), next);
        },
        function (next) {

            t.is(getStepNumber(wizard), 2, "Third step must have been activated.");
            t.ok(getField(wizard, "#textFour").getActionEl().hasCls('x-form-focus'), "First field must have been focused");

            t.waitForEvent(wizard, 'finished', next);
            wizard.next();
        },
        function (next) {

            var data = wizard.getData();
            t.isDeeply(data, {
                "textOne": 1,
                "textTwo": false,
                "textThree": "Three",
                "textFour": ""
            }, 'getData() method must have returned all data stored in wizard');

            t.waitForEvent(wizard, 'stepchanged', next);
            wizard.navigate(0);
        },
        function (next) {

            t.is(getStepNumber(wizard), 0, "First step must have been activated.");
            t.ok(getField(wizard, "#textOne").getActionEl().hasCls('x-form-focus'), "First field must have been focused");

            t.waitForEvent(wizard, 'validitychange', next);
            t.type(textOne.getActionEl(), '[BACKSPACE]');
        },
        function (next) {

            t.notOk(wizard.isWizardValid, "Wizard must have been not valid after clearing mandatory field.");
            t.notOk(wizard.isStepValid(), "Wizard first step must have been not valid after clearing mandatory field.");

            textOne.reset();
            t.waitForEvent(wizard, 'stepchanged', next);
            wizard.next();
        },
        function (next) {

            t.is(getStepNumber(wizard), 1, "Second step must have been activated.");
            t.ok(getField(wizard, "#textThree").getActionEl().hasCls('x-form-focus'), "First field must have been focused");

            t.waitForEvent(wizard, 'dirtychange', next);
            textThree.reset();

        },
        function (next) {

            t.notOk(wizard.isWizardDirty, "Wizard must have been not dirty after setting field values to original ones.");

            t.done();
        }
    );

}

StartTest(function (t) {
    t.requireOk(
        [
            'Admin.view.WizardPanel'
        ],
        function () {

            var comboStore = Ext.create('Ext.data.Store', {
                fields: ['key', 'name'],
                data: [
                    {"key": "1", "name": "One"},
                    {"key": "2", "name": "Two"},
                    {"key": "3", "name": "Three"}
                ]
            });

            var wizard = Ext.create('widget.wizardPanel', {
                renderTo: Ext.getBody(),
                isNew: true,
                items: [
                    {
                        stepTitle: 'stepOne',
                        xtype: 'form',
                        items: [
                            {
                                xtype: 'textfield',
                                fieldLabel: 'textOne',
                                name: 'textOne',
                                itemId: 'textOne',
                                value: '1',
                                allowBlank: false
                            },
                            {
                                xtype: 'checkboxfield',
                                fieldLabel: 'textTwo',
                                boxLabel: 'not neccessary field',
                                itemId: 'textTwo',
                                name: 'textTwo'
                            }
                        ]
                    },
                    {
                        title: 'stepTwo',
                        xtype: 'form',
                        items: [
                            {
                                xtype: 'combobox',
                                fieldLabel: 'textThree',
                                name: 'textThree',
                                itemId: 'textThree',
                                store: comboStore,
                                queryMode: 'local',
                                displayField: 'name',
                                valueField: 'key',
                                allowBlank: false
                            }
                        ]
                    },
                    {
                        title: 'stepThree',
                        xtype: 'form',
                        items: [
                            {
                                xtype: 'textarea',
                                fieldLabel: 'textFour',
                                name: 'textFour',
                                itemId: 'textFour'
                            }
                        ]
                    }
                ]
            });

            testWizard(t, wizard);

        }
    );
});