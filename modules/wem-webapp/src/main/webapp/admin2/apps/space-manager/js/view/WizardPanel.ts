module app_ui {

    export class WizardPanel {
        ext:any;

        private externalControls:any;
        private showControls:bool;
        public  data:any; // protected
        private isNew:bool;

        // items common for all steps that shall be valid for step to be valid
        public validateItems:any[]; // protected
        // items common for all steps that shall be disabled if step is invalid
        private boundItems:any[];

        // private, for storing wizard validity and dirty state; to be able to fire change event
        private isWizardValid:bool;
        private isWizardDirty:bool;
        // private, for tracking invalid and dirty items
        private dirtyItems:any;
        private invalidItems:any;
        private presentationMode:bool;
        private wizard:any; // Ext.container.Container

        constructor(config?:any) {
            this.showControls = true;
            this.isNew = true;
            this.validateItems = [];
            this.boundItems = [];
            this.presentationMode = false;
            this.dirtyItems = [];
            this.invalidItems = [];
            this.boundItems = [];

            var defaultPanelConfig = {
                itemId: 'wizardPanel',
                layout: {
                    type: 'vbox',
                    align: 'stretch'
                },
                cls: 'admin-wizard'
            };
            var panelConfig = Ext.apply({}, config, defaultPanelConfig);
            var panel = new Ext.panel.Panel(panelConfig);

            this.ext = panel;
            // reference to typescript wrapper object to have access to this object from ext component
            this.ext.wrapper = this;
            this.initComponent();
        }

        initComponent() {
            var me = this.ext;
            var events = [
                "beforestepchanged",
                "stepchanged",
                "animationstarted",
                "animationfinished",
                'validitychange',
                'dirtychange',
                "finished"
            ];
            me.cls += this.isNew ? ' admin-wizard-new' : ' admin-wizard-edit';

            this.wizard = new Ext.container.Container({
                region: 'center',
//            layout: new app_ui.WizardLayout('none').ext,
                layout: {
                    type: 'wizard',
                    animation: 'none'
                },
                items: this.createSteps()
            });

            var items = <any[]> [
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
                                    fn: () => {
                                        this.prev();
                                    }
                                },
                                mouseover: {
                                    element: 'el',
                                    fn: (event, element) => {
                                        this.updateNavButton(element, '#000000');
                                    }
                                },
                                mouseout: {
                                    element: 'el',
                                    fn: (event, element) => {
                                        this.updateNavButton(element, '#777777');
                                    }
                                }
                            },
                            items: [
                                {
                                    xtype: 'button',
                                    itemId: 'prev',
                                    iconCls: 'wizard-nav-icon icon-chevron-left icon-6x',
                                    cls: 'wizard-nav-button wizard-nav-button-left',
                                    height: 74,
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
                                    fn: ()  => {
                                        this.next();
                                    }
                                },
                                mouseover: {
                                    element: 'el',
                                    fn: (event, element) => {
                                        this.updateNavButton(element, '#000000');
                                    }
                                },
                                mouseout: {
                                    element: 'el',
                                    fn: (event, element) => {
                                        this.updateNavButton(element, '#777777');
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
                                    height: 74,
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
                            fn: () => {
                                this.updateShadow();
                            }
                        }
                    }
                }
            ];
            me.add(items);

//            me.callParent(arguments);
            me.addEvents(events);
            this.wizard.addEvents(events);
            this.wizard.enableBubble(events);
            me.on({
                animationstarted: this.onAnimationStarted,
                animationfinished: this.onAnimationFinished,
                resize: () => {
                    this.updateShadow();
                },
                scope: this
            });
            if (this.getActionButton()) {
                this.boundItems.push(this.getActionButton());
            }
            me.down('#progressBar').update(this.wizard.items.items);

            // bind afterrender events
            me.on('afterrender', this.bindItemListeners, this);

            this.updateShadow();
        }


        updateShadow() {
            var me = this.ext;
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
        }

        updateNavButton(element, color) {
            var btn = Ext.get(element);
            if (!btn.hasCls('wizard-nav-icon')) {
                btn = btn.down('.wizard-nav-icon');
            } else if (btn.hasCls('x-btn-inner')) {
                btn = btn.next('.x-btn-icon');
            }
            btn && btn.setStyle('color', color);
        }

        updateProgress(newStep?) {
            var me = this.ext;
            var progressBar = me.down('#progressBar');
            progressBar.update(this.wizard.items.items);
            var conditionsMet = this.isWizardValid && (this.isWizardDirty || this.isNew);
            progressBar.setDisabled(this.isNew ? !this.isStepValid(newStep) : !conditionsMet);
        }


        bindItemListeners(cmp) {
            Ext.each(this.validateItems, (validateItem, index, all) => {
                if (validateItem) {
                    validateItem.on({
                        'validitychange': this.handleValidityChange,
                        'dirtychange': this.handleDirtyChange,
                        scope: this
                    }, this);
                }
                return true;
            });
            var checkValidityFn = function (panel) {
                panel.getForm().checkValidity();
            };
            this.wizard.items.each((item, i) => {
                if (i === 0) {
                    this.onAnimationFinished(item, null);
                }
                if ('editUserFormPanel' === item.getXType()) {
                    item.on('fieldsloaded', checkValidityFn);
                }

                var itemForm = Ext.isFunction(item.getForm) ? item.getForm() : undefined;
                if (itemForm) {
                    if (Ext.isFunction(this.washDirtyForm)) {
                        this.washDirtyForm(itemForm);  // after load
                    }

                    Ext.apply(itemForm, {
                        onValidityChange: this.formOnValidityChange,
                        _boundItems: undefined
                    });
                    itemForm.on({
                        'validitychange': this.handleValidityChange,
                        'dirtychange': this.handleDirtyChange,
                        scope: this
                    });
                    itemForm.checkValidity();
                }
            });

        }

        formOnValidityChange() {
            var me = this.ext;
            var wizardPanel = me.owner.up('wizardPanel');
            var boundItems = wizardPanel.getFormBoundItems(this);
            if (boundItems && me.owner === wizardPanel.getActiveItem()) {
                var valid = wizardPanel.isStepValid(me.owner);
                boundItems.each((cmp) => {
                    if (cmp.rendered && cmp.isHidden() === valid) {
                        if (valid) {
                            cmp.show();
                        } else {
                            cmp.hide();
                        }
                    }
                });
            }
        }

        getFormBoundItems(form) {
            var boundItems = form._boundItems;
            if (!boundItems && form.owner.rendered) {
                boundItems = form._boundItems = Ext.create('Ext.util.MixedCollection');
                boundItems.addAll(form.owner.query('[formBind]'));
                boundItems.addAll(this.boundItems);
            }
            return boundItems;
        }

        handleValidityChange(form, valid, opts) {

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
                this.ext.fireEvent('validitychange', this, isWizardValid);
            }
        }

        handleDirtyChange(form, dirty, opts) {

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
                this.ext.fireEvent('dirtychange', this, isWizardDirty);
            }
        }

        isStepValid(step) {
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
        }

        getProgressBar() {
            return this.ext.down('#progressBar');
        }

        createRibbon() {
            var me = this;
            var stepsTpl = '<div class="navigation-container">' +
                           '<ul class="navigation clearfix">' +
                           '<tpl for=".">' +
                           '<li class="{[ this.resolveClsName( xindex, xcount ) ]}" wizardStep="{[xindex]}">' +
                           '<a href="javascript:;" class="step {[ this.resolveClsName( xindex, xcount ) ]}">{[' +
                           '(values.stepTitle || values.title) ]}</a></li>' +
                           '</tpl>' +
                           '</ul>' +
                           '</div>';
            return {
                xtype: 'component',
                flex: 1,
                cls: 'toolbar',
                disabledCls: 'toolbar-disabled',
                itemId: 'progressBar',
                width: '100%',
                listeners: {
                    click: {
                        fn: me.changeStep,
                        element: 'el',
                        scope: me
                    }
                },
                styleHtmlContent: true,
                margin: 0,
                tpl: new Ext.XTemplate(stepsTpl, {

                    resolveClsName: (index, total) => {
                        var activeIndex = me.wizard.items.indexOf(this.getActiveItem()) + 1;
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
        }

        onAnimationStarted(newStep, oldStep) {
            if (this.showControls) {
                // disable internal controls if shown
                this.updateButtons(this.wizard, true);
            }
            if (this.externalControls) {
                // try to disable external controls
                this.updateButtons(this.externalControls, true);
            }
        }

        onAnimationFinished(newStep, oldStep) {
            var me = this.ext;
            if (newStep) {
                this.updateProgress(newStep);
                this.focusFirstField(newStep);
                me.fireEvent("stepchanged", this, oldStep, newStep);
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
                me.doLayout();
                return newStep;
            }
            return null;
        }

        focusFirstField(newStep) {
            var activeItem = newStep || this.getActiveItem();
            var firstField;
            if (activeItem && (firstField = activeItem.down('field[disabled=false]'))) {
                firstField.focus(false);
                // Deselect text, for unknown reason text is always selected when focus is gained
                if (firstField.rendered && firstField.selectText) {
                    firstField.selectText(0, 0);
                }
            }
        }

        updateButtons(toolbar, disable?) {
            var me = this.ext;
            if (toolbar) {
                var prev = me.down('#prev'),
                    next = me.down('#next');
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
        }

        changeStep(event, target) {
            var me = this.ext;
            var progressBar = me.down('#progressBar');
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
        }

        createHeaderPanel() {
            var icon = this.createIcon();
            var actionButton = Ext.apply(this.createActionButton(), {
                itemId: 'actionButton',
                ui: 'green',
                margin: '0 30 0 0',
                scale: 'large'
            });

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
                            actionButton
                        ]
                    }

                ]
            };
        }

        getActionButton() {
            return this.ext.down('#actionButton');
        }

        next(btn?) {
            return this.navigate("next", btn);
        }

        prev(btn?) {
            return this.navigate("prev", btn);
        }

        finish() {
            this.ext.fireEvent("finished", this, this.getData());
        }

        getNext() {
            return this.wizard.getLayout().getNext();
        }

        getPrev() {
            return this.wizard.getLayout().getPrev();
        }

        getActiveItem() {
            return this.wizard.getLayout().getActiveItem();
        }

        navigate(direction, btn?) {
            var oldStep = this.getActiveItem();
            if (btn) {
                this.externalControls = btn.up('toolbar');
            }
            if (this.ext.fireEvent("beforestepchanged", this, oldStep) !== false) {
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
        }

        addData(newValues) {
            if (Ext.isEmpty(this.data)) {
                this.data = {};
            }
            Ext.merge(this.data, newValues);
        }

        deleteData(key) {
            if (key) {
                delete this.data[key];
            }
        }


        getData() {
            this.wizard.items.each((item) => {
                if (item.getData) {
                    this.addData(item.getData());
                } else if (item.getForm) {
                    this.addData(item.getForm().getFieldValues());
                }
            });
            return this.data;
        }

        /*
         * This method should be implemented in child classes
         */
        createSteps() {
            return null;
        }

        /*
         * This method should be implemented in child classes
         */
        createIcon() {
            return null;
        }

        /*
         * This method should be implemented in child classes
         */
        createWizardHeader() {
            return null;
        }

        createActionButton() {
            return null;
        }

        washDirtyForm(form) {
            return null;
        }

        getWizardDirty():bool {
            return this.isWizardDirty;
        }

    }
}
