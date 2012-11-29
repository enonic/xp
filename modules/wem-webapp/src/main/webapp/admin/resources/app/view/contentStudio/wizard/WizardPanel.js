Ext.define('Admin.view.contentStudio.wizard.WizardPanel', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.contentStudioWizardPanel',
    requires: [
        'Admin.view.WizardPanel',
        'Admin.view.contentStudio.wizard.ContentTypePanel',
        'Admin.view.contentStudio.wizard.ConfigPanel',
        'Admin.view.SummaryTreePanel',
        'Admin.plugin.fileupload.PhotoUploadButton',
        'Admin.view.contentStudio.wizard.Toolbar'
    ],
    layout: 'column',
    border: 0,
    autoScroll: true,
    defaults: {
        border: false
    },

    EMPTY_DISPLAY_NAME_TEXT: 'Display Name',

    initComponent: function () {
        var me = this;
        var steps = me.getSteps();
        var isNew = this.isNewContentType();

        var displayNameValue = 'Display Name';
        if (me.modelData) {
            displayNameValue = me.modelData.displayName || me.modelData.name;

        }
        me.headerData = {
            displayName: displayNameValue
        };

        me.tbar = Ext.createByAlias( 'widget.contentStudioWizardToolbar', {
            isNew: isNew
        } );

        me.items = [
            {
                width: 121,
                padding: 9,
                items: [
                    {
                        xtype: 'photoUploadButton',
                        width: 111,
                        height: 111,
                        photoUrl: "resources/images/icons/128x128/cubes.png",
                        title: "Content",
                        style: {
                            margin: '1px'
                        },
                        progressBarHeight: 6,
                        listeners: {
                            mouseenter: function () {
                                var imageToolTip = me.down('#imageToolTip');
                                imageToolTip.show();
                            },
                            mouseleave: function () {
                                var imageToolTip = me.down('#imageToolTip');
                                imageToolTip.hide();
                            }
                        }
                    },
                    {
                        styleHtmlContent: true,
                        height: 50,
                        border: 0,
                        itemId: 'imageToolTip',
                        style: {
                            top: '141px',
                            left: '10px'
                        },
                        cls: 'admin-image-upload-button-image-tip',
                        html: '<div class="x-tip x-tip-default x-layer" role="tooltip">' +
                              '<div class="x-tip-anchor x-tip-anchor-top"></div>' +
                              '<div class="x-tip-body  x-tip-body-default x-tip-body-default">' +
                              'Click to upload icon</div></div>',
                        listeners: {
                            afterrender: function (cmp) {
                                Ext.Function.defer(function () {
                                    cmp.hide();
                                }, 10000);
                            }
                        }
                    }
                ]
            },
            {
                columnWidth: 1,
                padding: '10 10 10 0',
                defaults: {
                    border: false
                },
                items: [
                    {
                        xtype: 'container',
                        itemId: 'wizardHeader',
                        styleHtmlContent: true,
                        autoHeight: true,
                        cls: 'admin-wizard-header-container',
                        listeners: {
                            afterrender: function (header) {
                                me.headerRendered = true;
                                me.bindDisplayNameEvents();
                            }
                        },
                        tpl: new Ext.XTemplate(Templates.contentStudio.wizardHeader),
                        data: me.headerData
                    },
                    {
                        xtype: 'wizardPanel',
                        showControls: false,
                        isNew: isNew,
                        items: steps
                    }
                ]
            }
        ];

        this.callParent(arguments);
    },


    getSteps: function () {
        var me = this;

        var configStep = {
            stepTitle: 'Content Type',
            modelData: me.modelData,
            xtype: 'contentStudioWizardConfigPanel',
            listeners: {
                afterrender: function (panel) {
                    me.panelRendered = true;
                    me.bindDisplayNameEvents();
                }
            }
        };

        return [configStep];
    },


    removeEmptySteps: function (wizardPanel) {
        wizardPanel.items.each(function (item) {
            if (!item.alwaysKeep && item.getForm && (item.getForm().getFields().getCount() === 0)) {
                wizardPanel.remove(item);
            }
        });
    },


    isNewContentType: function () {
        return this.modelData === undefined;
    },


    getWizardPanel: function () {
        return this.down('wizardPanel');
    },


    getData: function () {
        return this.getWizardPanel().getData();
    },


    bindDisplayNameEvents: function () {
        if (this.headerRendered && this.panelRendered) {

            var displayName = this.el.down('input.admin-display-name', false);
            if (displayName) {
                displayName.on('focus', this.onDisplayNameFocused, this);
                displayName.on('blur', this.onDisplayNameBlur, this);
            }

            var name = this.down('contentStudioWizardContentTypePanel #name');
            if (name) {
                name.on('change', this.onNameChanged, this);
            }

            this.el.on('click', this.toggleDisplayNameField, this);
        }
    },

    onNameChanged: function (field, newVal, oldVal, opts) {
        if (!this.headerEdited) {
            this.setDisplayName(newVal, false);
        }
    },

    toggleDisplayNameField: function (event, target, opts) {
        var clickedElement = new Ext.Element(target);
        var displayName;
        if (clickedElement.hasCls('admin-display-name')) {
            displayName = clickedElement;
            displayName.dom.removeAttribute('readonly');
            displayName.addCls('admin-edited-field');
        } else {
            displayName = this.el.down('input.admin-display-name', false);
            displayName.set({readonly: true});
            var value = Ext.String.trim(displayName.getValue());
            if (value === '' || value === this.EMPTY_DISPLAY_NAME_TEXT) {
                displayName.removeCls('admin-edited-field');
            }
        }
    },

    onDisplayNameFocused: function (event, element) {
        if (Ext.String.trim(element.value) === this.EMPTY_DISPLAY_NAME_TEXT) {
            element.value = '';
        }
    },

    onDisplayNameBlur: function (event, element, opts) {
        this.setDisplayName(element.value, true);
    },

    setDisplayName: function (text, updateEdited) {
        text = this.processDisplayName(text);
        var displayName = this.el.down('input.admin-display-name', false);
        if (Ext.isEmpty(text)) {
            displayName.dom.value = this.EMPTY_DISPLAY_NAME_TEXT;
            if (updateEdited) {
                this.headerEdited = false;
            }
        } else {
            displayName.dom.value = text;
            Ext.apply(this.headerData, {
                displayName: text
            });
            if (updateEdited) {
                this.headerEdited = true;
            }
        }
    },

    processDisplayName: function (string) {
        string = Ext.String.trim(string);
        return string.length > 0 ? Ext.String.capitalize(string) : "";
    }

});
