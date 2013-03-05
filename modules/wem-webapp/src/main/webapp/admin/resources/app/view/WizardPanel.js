Ext.define('Admin.view.WizardPanel', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.wizardPanel',

    requires: ['Admin.view.WizardLayout'],

    layout: {
        type: 'wizard',
        animation: 'none'
    },
    cls: 'admin-wizard',
    autoHeight: true,
    defaults: {
        border: false,
        frame: false,
        autoHeight: true
    },
    bodyPadding: 10,
    minWidth: 500,

    externalControls: undefined,
    showControls: true,
    data: {},
    isNew: true,

    // items common for all steps that shall be valid for step to be valid
    validateItems: [],
    // items common for all steps that shall be disabled if step is invalid
    boundItems: [],

    // private, for storing wizard validity and dirty state, to be able to fire change event
    isWizardValid: undefined,
    isWizardDirty: undefined,
    // private, for tracking invalid and dirty items
    dirtyItems: undefined,
    invalidItems: undefined,
    presentationMode: false,


    initComponent: function () {
        var wizard = this;
        this.data = {};
        this.dirtyItems = [];
        this.invalidItems = [];

        this.cls += this.isNew ? ' admin-wizard-new' : ' admin-wizard-edit';

        if (this.showControls) {
            wizard.bbar = {
                xtype: 'container',
                margin: '10 0 0 5',
                height: 40,
                itemId: 'controls',
                defaults: {
                    xtype: 'button'
                },
                items: [
                    {
                        itemId: 'prev',
                        iconCls: 'icon-chevron-left icon-4x',
                        margin: '0 5 0 0',
                        cls: 'wizard-nav-button wizard-nav-button-left',
                        /*hideMode: 'display',*/
                        height: 64,
                        width: 64,
                        handler: function (btn, evt) {
                            wizard.prev();
                        }
                    },
                    {
                        itemId: 'next',
                        iconAlign: 'right',
                        margin: '0 0 0 5',
                        cls: 'wizard-nav-button wizard-nav-button-right',
                        formBind: true,
                        iconCls: 'icon-chevron-right icon-4x',
                        height: 64,
                        width: 64,
                        handler: function (btn, evt) {
                            wizard.next();
                        }
                    },
                    {
                        text: 'Save and Close',
                        itemId: 'finish',
                        margin: '0 0 0 5',
                        iconCls: 'icon-save-24',
                        hidden: true,
                        handler: function (btn, evt) {
                            wizard.finish();
                        }
                    }
                ]
            };

        }

        this.dockedItems = [
            {
                xtype: 'panel',
                dock: 'top',
                cls: 'toolbar',
                disabledCls: 'toolbar-disabled',
                itemId: 'progressBar',
                listeners: {
                    click: {
                        fn: wizard.changeStep,
                        element: 'body',
                        scope: wizard
                    }
                },
                styleHtmlContent: true,
                margin: 0,
                tpl: new Ext.XTemplate(Templates.common.wizardPanelSteps, {

                    resolveClsName: function (index, total) {
                        var activeIndex = wizard.items.indexOf(wizard.getLayout().getActiveItem()) + 1;
                        var clsName = '';

                        if (index === 1) {
                            clsName += 'first ';
                        }

                        if (index < activeIndex) {
                            clsName += 'previous ';
                        }

                        if (index + 1 === activeIndex) {
                            clsName += 'immediate ';
                        }

                        if (index === activeIndex) {
                            clsName += 'current ';
                        }

                        if (index > activeIndex) {
                            clsName += 'next ';
                        }

                        if (index - 1 === activeIndex) {
                            clsName += 'immediate ';
                        }

                        if (index === total) {
                            clsName += 'last ';
                        }

                        return clsName;
                    }
                })
            }
        ];

        this.callParent(arguments);
        this.updateProgress();
        this.addEvents(
            "beforestepchanged",
            "stepchanged",
            "animationstarted",
            "animationfinished",
            'validitychange',
            'dirtychange',
            "finished"
        );
        this.on({
            animationstarted: this.onAnimationStarted,
            animationfinished: this.onAnimationFinished
        });

        if (this.showControls) {
            var controls = this.getDockedComponent('controls');
            this.boundItems.push(controls.down('#next'));
        }

        // bind afterrender events
        this.on('afterrender', this.bindItemListeners);

    },

    bindItemListeners: function (cmp) {
        var i;
        for (i = 0; i < cmp.validateItems.length; i++) {
            var validateItem = cmp.validateItems[i];
            if (validateItem) {
                validateItem.on({
                    'validitychange': cmp.handleValidityChange,
                    'dirtychange': cmp.handleDirtyChange,
                    scope: cmp
                }, this);
            }
        }
        var checkValidityFn = function (panel) {
            panel.getForm().checkValidity();
        };
        for (i = 0; i < cmp.items.items.length; i++) {
            var item = cmp.items.items[i];
            if (i === 0) {
                cmp.onAnimationFinished(item, null);
            }
            if ('editUserFormPanel' === item.getXType()) {
                item.on('fieldsloaded', checkValidityFn);
            }

            var itemForm = Ext.isFunction(item.getForm) ? item.getForm() : undefined;
            if (itemForm) {
                Ext.apply(itemForm, {
                    onValidityChange: cmp.formOnValidityChange,
                    _boundItems: undefined
                });
                itemForm.on({
                    'validitychange': cmp.handleValidityChange,
                    'dirtychange': cmp.handleDirtyChange,
                    scope: cmp
                });
                itemForm.checkValidity();
            }
        }

    },

    formOnValidityChange: function () {
        var wizard = this.owner.up('wizardPanel');
        var boundItems = wizard.getFormBoundItems(this);
        if (boundItems && this.owner === wizard.getLayout().getActiveItem()) {
            var valid = wizard.isStepValid(this.owner);
            boundItems.each(function (cmp) {
                if (cmp.rendered && cmp.isHidden() === valid) {
                    if (valid) {
                        cmp.show();
                    } else {
                        cmp.hide();
                    }
                }
            });
        }
    },

    getFormBoundItems: function (form) {
        var boundItems = form._boundItems;
        if (!boundItems && form.owner.rendered) {
            boundItems = form._boundItems = Ext.create('Ext.util.MixedCollection');
            boundItems.addAll(form.owner.query('[formBind]'));
            boundItems.addAll(this.boundItems);
        }
        return boundItems;
    },

    handleValidityChange: function (form, valid, opts) {

        if (!valid) {
            Ext.Array.include(this.invalidItems, form);
        } else {
            Ext.Array.remove(this.invalidItems, form);
        }

        this.updateProgress();

        var isWizardValid = this.invalidItems.length === 0;
        if (this.isWizardValid !== isWizardValid) {
            // fire the wizard validity change event
            this.isWizardValid = isWizardValid;
            this.fireEvent('validitychange', this, isWizardValid);
        }
    },

    handleDirtyChange: function (form, dirty, opts) {

        if (dirty) {
            Ext.Array.include(this.dirtyItems, form);
        } else {
            Ext.Array.remove(this.dirtyItems, form);
        }

        this.updateProgress();

        var isWizardDirty = this.dirtyItems.length > 0;
        if (this.isWizardDirty !== isWizardDirty) {
            // fire the wizard dirty change event
            this.isWizardDirty = isWizardDirty;
            this.fireEvent('dirtychange', this, isWizardDirty);
        }
    },


    changeStep: function (event, target) {
        var progressBar = this.dockedItems.items[0];
        var isNew = this.isNew;
        var isDisabled = progressBar.isDisabled();

        var li = target && target.tagName === "LI" ? Ext.fly(target) : Ext.fly(target).up('li');

        // allow click only the next immediate step in new mode
        // or any step in edit mode when valid
        // or any except the last in edit when not valid
        // or all previous steps in any mode
        if ((!isDisabled && isNew && li && li.hasCls('next') && li.hasCls('immediate'))
                || (!isDisabled && !isNew)
                || (isDisabled && !isNew && li && !li.hasCls('last'))
            || (li && li.hasCls('previous'))) {
            var step = Number(li.getAttribute('wizardStep'));
            this.navigate(step - 1);
        }
        event.stopEvent();
    },

    next: function (btn) {
        return this.navigate("next", btn);
    },

    prev: function (btn) {
        return this.navigate("prev", btn);
    },

    finish: function () {
        this.fireEvent("finished", this, this.getData());
    },

    getNext: function () {
        return this.getLayout().getNext();
    },

    getPrev: function () {
        return this.getLayout().getPrev();
    },

    navigate: function (direction, btn) {
        var oldStep = this.getLayout().getActiveItem();
        if (btn) {
            this.externalControls = btn.up('toolbar');
        }
        if (this.fireEvent("beforestepchanged", this, oldStep) !== false) {
            var newStep;
            switch (direction) {
            case "-1":
            case "prev":
                if (this.getPrev()) {
                    newStep = this.getLayout().prev();
                }
                break;
            case "+1":
            case "next":
                if (this.getNext()) {
                    newStep = this.getLayout().next();
                } else {
                    this.finish();
                }
                break;
            default:
                newStep = this.getLayout().setActiveItem(direction);
                break;
            }
        }
    },

    onAnimationStarted: function (newStep, oldStep) {
        if (this.showControls) {
            // disable internal controls if shown
            this.updateButtons(this.getDockedComponent('controls'), true);
        }
        if (this.externalControls) {
            // try to disable external controls
            this.updateButtons(this.externalControls, true);
        }
    },

    onAnimationFinished: function (newStep, oldStep) {
        if (newStep) {
            this.updateProgress(newStep);
            this.focusFirstField(newStep);
            this.fireEvent("stepchanged", this, oldStep, newStep);
            if (this.showControls) {
                // update internal controls if shown
                this.updateButtons(this.getDockedComponent('controls'));
            }
            if (this.externalControls) {
                // try to update external controls
                this.updateButtons(this.externalControls);
            }

            // TODO: Review - should we do this when a step does not have form?
            if (Ext.isFunction(newStep.getForm)) {
                var newForm = newStep.getForm();
                if (newForm) {
                    newForm.onValidityChange(this.isStepValid(newStep));
                }
            }
            this.doLayout();
            return newStep;
        }
    },

    updateProgress: function (newStep) {
        var progressBar = this.dockedItems.items[0];
        progressBar.update(this.items.items);
        var conditionsMet = this.isWizardValid && (this.isWizardDirty || this.isNew);
        progressBar.setDisabled(this.isNew ? !this.isStepValid(newStep) : !conditionsMet);
    },

    isStepValid: function (step) {
        var isStepValid = Ext.Array.intersect(this.invalidItems, this.validateItems).length === 0;
        var activeStep = step || this.getLayout().getActiveItem();
        var activeForm;
        if (activeStep && Ext.isFunction(activeStep.getForm)) {
            activeForm = activeStep.getForm();
        }
        if (isStepValid && activeForm) {
            isStepValid = isStepValid && !activeForm.hasInvalidField();
        }
        return isStepValid;
    },

    updateButtons: function (toolbar, disable) {
        if (toolbar) {
            var prev = toolbar.down('#prev'),
                next = toolbar.down('#next');
            var hasNext = this.getNext(),
                hasPrev = this.getPrev();
            if (prev) {
                if (disable || !hasPrev) {
                    prev.hide();
                } else {
                    prev.show();
                }
            }
            if (next) {
                if (disable || !hasNext) {
                    next.hide();
                } else {
                    next.show();
                }
                next.removeCls('admin-prev-button');
                next.removeCls('admin-button');
                next.addCls(hasPrev ? 'admin-prev-button' : 'admin-button');
            }
        }
    },

    focusFirstField: function (newStep) {
        var activeItem = newStep || this.getLayout().getActiveItem();
        var firstField;
        if (activeItem && (firstField = activeItem.down('field[disabled=false]'))) {
            firstField.focus();
        }
    },

    addData: function (newValues) {
        Ext.merge(this.data, newValues);
    },

    deleteData: function (key) {
        if (key) {
            delete this.data[key];
        }
    },


    getData: function () {
        var me = this;
        me.items.each(function (item) {
            if (item.getData) {
                me.addData(item.getData());
            } else if (item.getForm) {
                me.addData(item.getForm().getFieldValues());
            }
        });
        return me.data;
    },

    getProgressBar: function () {
        return this.dockedItems.items[0];
    }

});
