Ext.define('Admin.view.WizardPanel', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.wizardPanel',

    requires: ['Admin.view.WizardLayout'],

    layout: {
        type: 'vbox',
        align: 'stretch'
    },

    cls: 'admin-wizard',

    externalControls: undefined,
    showControls: true,
    data: undefined,
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
        var me = this;
        var events = [
            "beforestepchanged",
            "stepchanged",
            "animationstarted",
            "animationfinished",
            'validitychange',
            'dirtychange',
            "finished"
        ];
        this.dirtyItems = [];
        this.invalidItems = [];
        this.boundItems = [];
        this.cls += this.isNew ? ' admin-wizard-new' : ' admin-wizard-edit';

        this.wizard = Ext.createByAlias('widget.container', {
            region: 'center',
            layout: {
                type: 'wizard',
                animation: 'none'
            },
            items: this.createSteps()
        });
        this.items = [
            this.createHeaderPanel(),
            {
                itemId: 'bottomPanel',
                xtype: 'container',
                autoScroll: true,
                padding: '20 0 0 0',
                layout: 'border',
                flex: 1,
                items: [
                    {
                        xtype: 'container',
                        region: 'west',
                        padding: 10,
                        width: 130,
                        style: {
                            position: 'fixed !important',
                            top: '210px !important'
                        },
                        layout: {
                            type: 'hbox',
                            align: 'middle'
                        },
                        listeners: {
                            click: {
                                element: 'el',
                                fn: function () {
                                    me.prev();
                                }
                            },
                            mouseover: {
                                element: 'el',
                                fn: function (event, element) {
                                    me.updateNavButton(element, '#000000');
                                }
                            },
                            mouseout: {
                                element: 'el',
                                fn: function (event, element) {
                                    me.updateNavButton(element, '#777777');
                                }
                            }
                        },
                        items: [
                            {
                                xtype: 'button',
                                itemId: 'prev',
                                iconCls: 'wizard-nav-icon icon-chevron-left icon-6x',
                                cls: 'wizard-nav-button wizard-nav-button-left',
                                height: 64,
                                width: 64,
                                padding: 0,
                                margin: '0 0 0 40'
                            }
                        ]
                    },
                    {
                        xtype: 'container',
                        region: 'east',
                        padding: 10,
                        width: 130,
                        style: {
                            position: 'fixed !important',
                            top: '210px !important'
                        },
                        layout: {
                            type: 'hbox',
                            align: 'middle'
                        },
                        listeners: {
                            click: {
                                element: 'el',
                                fn: function () {
                                    me.next();
                                }
                            },
                            mouseover: {
                                element: 'el',
                                fn: function (event, element) {
                                    me.updateNavButton(element, '#000000');
                                }
                            },
                            mouseout: {
                                element: 'el',
                                fn: function (event, element) {
                                    me.updateNavButton(element, '#777777');
                                }
                            }

                        },
                        items: [
                            {
                                xtype: 'button',
                                itemId: 'next',
                                iconAlign: 'right',
                                cls: 'wizard-nav-button wizard-nav-button-right',
                                formBind: true,
                                iconCls: 'wizard-nav-icon icon-chevron-right icon-6x',
                                height: 64,
                                width: 64,
                                padding: 0
                            }
                        ]
                    },
                    this.wizard
                ],
                listeners: {
                    scroll: {
                        element: 'el',
                        fn: function () {
                            me.updateShadow(me);
                        }
                    }
                }
            }
        ];

        Ext.EventManager.onWindowResize(function () {
            me.updateShadow(me);
        });

        this.callParent(arguments);
        this.addEvents(events);
        this.wizard.addEvents(events);
        this.wizard.enableBubble(events);
        this.on({
            animationstarted: this.onAnimationStarted,
            animationfinished: this.onAnimationFinished
        });
        if (this.getActionButton()) {
            this.boundItems.push(this.getActionButton());
        }
        this.down('#progressBar').update(this.wizard.items.items);

        // bind afterrender events
        this.on('afterrender', this.bindItemListeners);

        me.updateShadow(me);
    },

    updateShadow: function (me) {
        var bottomPanel = me.down('#bottomPanel').getEl();

        if (bottomPanel) {
            var hasScroll = bottomPanel.dom.scrollHeight > bottomPanel.dom.clientHeight,
                positionPanelEl = me.down('#positionPanel').getEl(),
                wizardHeaderPanelHeight = me.down('#wizardHeaderPanel').getEl().getHeight(),
                headerShadowEl = Ext.fly('admin-wizard-header-shadow');

            if (hasScroll && bottomPanel.dom.scrollTop !== 0) {
                if (!headerShadowEl) {
                    var dh = Ext.DomHelper,
                        boxShadowOffsets = Ext.isGecko ? '0 5px 6px -3px' : '0 5px 10px -3px';

                    var shadowDomSpec = {
                        id: 'admin-wizard-header-shadow',
                        tag: 'div',
                        style: 'position:absolute; top:' + wizardHeaderPanelHeight +
                               'px; left:0px; z-index:1000; height:10px; background:transparent; width:100%; box-shadow:' +
                               boxShadowOffsets + '#888 inset'
                    };

                    dh.append(positionPanelEl, shadowDomSpec);
                    Ext.fly('admin-wizard-header-shadow').show(true);
                }

            } else {
                if (headerShadowEl) {
                    headerShadowEl.remove();
                }
            }
        }
    },

    updateNavButton: function (element, color) {
        var btn = Ext.get(element);
        if (!btn.hasCls('wizard-nav-icon')) {
            btn = btn.down('.wizard-nav-icon');
        } else if (btn.hasCls('x-btn-inner')) {
            btn = btn.next('.x-btn-icon');
        }
        btn.setStyle('color', color);
    },

    updateProgress: function (newStep) {
        var progressBar = this.down('#progressBar');
        progressBar.update(this.wizard.items.items);
        var conditionsMet = this.isWizardValid && (this.isWizardDirty || this.isNew);
        progressBar.setDisabled(this.isNew ? !this.isStepValid(newStep) : !conditionsMet);
    },


    bindItemListeners: function (cmp) {
        Ext.each(cmp.validateItems, function (validateItem, i) {
            if (validateItem) {
                validateItem.on({
                    'validitychange': cmp.handleValidityChange,
                    'dirtychange': cmp.handleDirtyChange,
                    scope: cmp
                }, this);
            }
        });
        var checkValidityFn = function (panel) {
            panel.getForm().checkValidity();
        };
        cmp.wizard.items.each(function (item, i) {
            if (i === 0) {
                cmp.onAnimationFinished(item, null);
            }
            if ('editUserFormPanel' === item.getXType()) {
                item.on('fieldsloaded', checkValidityFn);
            }

            var itemForm = Ext.isFunction(item.getForm) ? item.getForm() : undefined;
            if (itemForm) {
                if (Ext.isFunction(cmp.washDirtyForm)) {
                    cmp.washDirtyForm(itemForm);  // after load
                }

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
        });

    },

    formOnValidityChange: function () {
        var wizardPanel = this.owner.up('wizardPanel');
        var boundItems = wizardPanel.getFormBoundItems(this);
        if (boundItems && this.owner === wizardPanel.getActiveItem()) {
            var valid = wizardPanel.isStepValid(this.owner);
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
            var actionButton = this.getActionButton();
            if (actionButton) {
                actionButton.setVisible(isWizardValid);
            }
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

    isStepValid: function (step) {
        var isStepValid = Ext.Array.intersect(this.invalidItems, this.validateItems).length === 0;
        var activeStep = step || this.getActiveItem();
        var activeForm;
        if (activeStep && Ext.isFunction(activeStep.getForm)) {
            activeForm = activeStep.getForm();
        }
        if (isStepValid && activeForm) {
            isStepValid = isStepValid && !activeForm.hasInvalidField();
        }
        return isStepValid;
    },

    getProgressBar: function () {
        return this.down('#progressBar');
    },

    createRibbon: function () {
        var me = this;

        return {
            xtype: 'component',
            flex: 1,
            cls: 'toolbar',
            disabledCls: 'toolbar-disabled',
            itemId: 'progressBar',
            width: '100%',
            listeners: {
                click: {
                    fn: this.changeStep,
                    element: 'el',
                    scope: this
                }
            },
            styleHtmlContent: true,
            margin: 0,
            tpl: new Ext.XTemplate(Templates.common.wizardPanelSteps, {

                resolveClsName: function (index, total) {
                    var activeIndex = me.wizard.items.indexOf(me.getActiveItem()) + 1;
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
        };
    },

    onAnimationStarted: function (newStep, oldStep) {
        if (this.showControls) {
            // disable internal controls if shown
            this.updateButtons(this.wizard, true);
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
                this.updateButtons(this.wizard);
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

    focusFirstField: function (newStep) {
        var activeItem = newStep || this.getActiveItem();
        var firstField;
        if (activeItem && (firstField = activeItem.down('field[disabled=false]'))) {
            firstField.focus(false);
            // Deselect text, for unknown reason text is always selected when focus is gained
            if (firstField.selectText) {
                firstField.selectText(0, 0);
            }
        }
    },

    updateButtons: function (toolbar, disable) {
        if (toolbar) {
            var prev = this.down('#prev'),
                next = this.down('#next');
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

    changeStep: function (event, target) {
        var progressBar = this.down('#progressBar');
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

    createHeaderPanel: function () {
        var icon = this.createIcon();
        return {
            xtype: 'container',
            itemId: 'wizardHeaderPanel',
            cls: 'admin-wizard-panel',
            padding: '10 0 0 10',
            margin: '0 0 0 0',
            layout: {
                type: 'table',
                columns: 2,
                tableAttrs: {
                    width: '100%'
                }
            },
            items: [
                Ext.applyIf(icon, {
                    rowspan: 2,
                    tdAttrs: {
                        style: 'padding-right: 10px'
                    }
                }),
                Ext.applyIf(this.createWizardHeader(), {
                    tdAttrs: { width: '100%'}
                }),
                {
                    itemId: 'positionPanel',
                    xtype: 'container',
                    style: {
                        backgroundColor: '#EEEEEE'
                    },
                    layout: {
                        type: 'hbox',
                        align: 'stretch'
                    },
                    tdAttrs: {
                        style: 'vertical-align: bottom;'
                    },
                    items: [
                        Ext.applyIf(this.createRibbon(), {
                            flex: 1
                        }),
                        Ext.apply(this.createActionButton(), {
                            itemId: 'actionButton',
                            ui: 'green',
                            margin: '0 30 0 0',
                            scale: 'large'
                        })
                    ]
                }

            ]
        };
    },

    getActionButton: function () {
        return this.down('#actionButton');
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
        return this.wizard.getLayout().getNext();
    },

    getPrev: function () {
        return this.wizard.getLayout().getPrev();
    },

    getActiveItem: function () {
        return this.wizard.getLayout().getActiveItem();
    },

    navigate: function (direction, btn) {
        var oldStep = this.getActiveItem();
        if (btn) {
            this.externalControls = btn.up('toolbar');
        }
        if (this.fireEvent("beforestepchanged", this, oldStep) !== false) {
            var newStep;
            switch (direction) {
            case "-1":
            case "prev":
                if (this.getPrev()) {
                    newStep = this.wizard.getLayout().prev();
                }
                break;
            case "+1":
            case "next":
                if (this.getNext()) {
                    newStep = this.wizard.getLayout().next();
                } else {
                    this.finish();
                }
                break;
            default:
                newStep = this.wizard.getLayout().setActiveItem(direction);
                break;
            }
        }
    },

    addData: function (newValues) {
        if (Ext.isEmpty(this.data)) {
            this.data = {};
        }
        Ext.merge(this.data, newValues);
    },

    deleteData: function (key) {
        if (key) {
            delete this.data[key];
        }
    },


    getData: function () {
        var me = this;
        me.wizard.items.each(function (item) {
            if (item.getData) {
                me.addData(item.getData());
            } else if (item.getForm) {
                me.addData(item.getForm().getFieldValues());
            }
        });
        return me.data;
    },

    /*
     * This method should be implemented in child classes
     */
    createSteps: function () {

    },

    /*
     * This method should be implemented in child classes
     */
    createIcon: function () {

    },

    /*
     * This method should be implemented in child classes
     */
    createWizardHeader: function () {

    },

    createActionButton: function () {

    }

});
