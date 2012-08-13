Ext.define('Admin.view.account.wizard.user.UserWizardPanel', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.userWizardPanel',
    requires: [
        'Admin.view.WizardPanel',
        'Admin.view.account.wizard.user.UserStoreListPanel',
        'Admin.view.account.wizard.user.UserWizardToolbar',
        'Admin.view.account.EditUserFormPanel',
        'Admin.view.account.wizard.user.WizardStepLoginInfoPanel',
        'Admin.view.account.wizard.user.WizardStepMembershipPanel',
        'Admin.view.SummaryTreePanel',
        'Admin.plugin.fileupload.PhotoUploadButton'
    ],

    layout: 'column',

    border: 0,
    autoScroll: true,

    defaults: {
        border: false
    },

    displayNameAutoGenerate: true,

    initComponent: function () {
        var me = this;
        var isNew = this.isNewUser();
        var photoUrl;
        var userGroups = [];
        var displayNameValue = 'Display Name';
        if (me.userFields) {
            photoUrl = Admin.plugin.UriHelper.getAccountIconUri(me.userFields);
            userGroups = me.userFields.groups;
            displayNameValue = me.userFields.displayName;

            this.preProcessAddresses(me.userFields);
        }
        me.headerData = {
            displayName: displayNameValue,
            userstoreName: me.userstore,
            qUserName: me.qUserName,
            isNewUser: isNew,
            edited: false
        };

        me.tbar = {
            xtype: 'userWizardToolbar',
            isNewUser: isNew
        };
        me.items = [
            {
                width: 121,
                padding: 5,
                items: [
                    {
                        xtype: 'photoUploadButton',
                        width: 111,
                        height: 111,
                        style: {
                            position: 'fixed',
                            top: '96px'
                        },
                        photoUrl: photoUrl,
                        title: "User",
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
                        cls: 'admin-image-upload-button-image-tip',
                        html: '<div class="x-tip x-tip-default x-layer" role="tooltip">' +
                              '<div class="x-tip-anchor x-tip-anchor-top"></div>' +
                              '<div class="x-tip-body  x-tip-body-default x-tip-body-default">' +
                              'Click to upload photo</div></div>',
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
                            afterrender: {
                                fn: function () {
                                    var me = this;
                                    me.getEl().addListener('click', function (event, target, eOpts) {
                                        me.toggleDisplayNameField(event, target);
                                    });
                                },
                                scope: this
                            }
                        },
                        tpl: new Ext.XTemplate(Templates.account.userWizardHeader),
                        data: me.headerData
                    },
                    {
                        xtype: 'wizardPanel',
                        showControls: true,
                        isNew: isNew,
                        items: [
                            {
                                stepTitle: "Profile",
                                itemId: "profilePanel",
                                xtype: 'editUserFormPanel',
                                userFields: me.userFields,
                                enableToolbar: false
                            },
                            {
                                stepTitle: "User",
                                itemId: "userPanel",
                                xtype: 'editUserFormPanel',
                                userFields: me.userFields,
                                includedFields: ['username', 'email', 'password', 'repeatPassword', 'photo',
                                    'country', 'locale', 'timezone', 'globalPosition'],
                                enableToolbar: false
                            },
                            {
                                stepTitle: "Places",
                                itemId: 'placesPanel',
                                xtype: 'editUserFormPanel',
                                includedFields: ['address'],
                                userFields: me.userFields,
                                enableToolbar: false
                            },
                            {
                                stepTitle: "Memberships",
                                groups: userGroups,
                                xtype: 'wizardStepMembershipPanel',
                                listeners: {
                                    afterrender: {
                                        fn: function () {
                                            var membershipPanel = this.down('wizardStepMembershipPanel');
                                            this.getWizardPanel().addData(membershipPanel.getData());
                                        },
                                        scope: this
                                    }
                                }
                            },
                            {
                                stepTitle: 'Summary',
                                dataType: 'user',
                                xtype: 'summaryTreePanel'
                            }
                        ]
                    }
                ]
            }
        ];

        this.callParent(arguments);

        var uploader = this.down('photoUploadButton');
        uploader.on('fileuploaded', me.photoUploaded, me);


        // Make wizard navigation sticky
        me.on('afterrender', function (userWizard) {
            this.addStickyNavigation(userWizard);
            //Render all user forms
            if (me.userFields && me.userFields.userStore) {
                me.renderUserForms(me.userFields.userStore);
            } else {
                me.renderUserForms(me.userstore);
            }
            me.removeEmptySteps(userWizard.getWizardPanel());
            // Set active item to first one. D-02010 bug workaround
            me.getWizardPanel().getLayout().setActiveItem(0);
        });
    },

    preProcessAddresses: function (userFields) {
        // assign each address a position to be able to reflect this in diff
        if (userFields.userInfo && userFields.userInfo.addresses) {
            var i;
            for (i = 0; i < userFields.userInfo.addresses.length; i++) {
                userFields.userInfo.addresses[i].originalIndex = i;
            }
        }
    },

    removeEmptySteps: function (wizardPanel) {
        wizardPanel.items.each(function (item) {
            if (!item.alwaysKeep && item.getForm && (item.getForm().getFields().getCount() === 0)) {
                wizardPanel.remove(item);
            }
        });
    },

    addStickyNavigation: function (wizardPanel) {
        wizardPanel.body.on('scroll', function () {
            // Ideally the element should be cached, but the navigation view is rendered (tpl.update()) for each step.
            var navigationElement = Ext.get(Ext.DomQuery.selectNode('.admin-wizard-navigation-container',
                wizardPanel.body.dom));
            var bodyScrollTop = wizardPanel.body.getScroll().top;
            if (bodyScrollTop > 73) {
                navigationElement.addCls('admin-wizard-navigation-container-sticky');
            } else {
                navigationElement.removeCls('admin-wizard-navigation-container-sticky');
            }
        }, wizardPanel);
    },

    toggleDisplayNameField: function (event, target) {
        var clickedElement = new Ext.Element(target);
        var parentToClickedElementIsHeader = clickedElement.findParent('.admin-wizard-header');
        var displayNameFieldElement = this.getEl().select('.admin-display-name').item(0);

        if (parentToClickedElementIsHeader) {
            displayNameFieldElement.dom.removeAttribute('readonly');
            displayNameFieldElement.addCls('admin-edited-field');
        } else {
            displayNameFieldElement.set({readonly: true});
            var value = Ext.String.trim(displayNameFieldElement.getValue());
            if (value === '' || value === 'Display Name') {
                displayNameFieldElement.removeCls('admin-edited-field');
            }
        }
    },

    resizeFileUpload: function (file) {
        file.el.down('input[type=file]').setStyle({
            width: file.getWidth(),
            height: file.getHeight()
        });
    },

    setFileUploadDisabled: function (disable) {
        //TODO: disable image upload
        //this.uploadForm.setDisabled( disable );
    },

    renderUserForms: function (userStore) {
        var userForms = this.query('editUserFormPanel');
        Ext.Array.each(userForms, function (userForm) {
            userForm.renderUserForm({userStore: userStore});
        });
    },

    updateHeader: function (data) {
        Ext.apply(this.headerData, data);
        this.down('#wizardHeader').update(this.headerData);
    },

    isNewUser: function () {
        return this.userFields === undefined;
    },

    getWizardPanel: function () {
        return this.down('wizardPanel');
    },

    getData: function () {
        var wizardData = this.getWizardPanel().getData();
        var displayNameField = this.el.select('input.admin-display-name').item(0);
        if (displayNameField) {
            var data = {displayName: displayNameField.getValue() };
            Ext.merge(wizardData, data);
        }
        return wizardData;
    },

    photoUploaded: function (photoUploadButton, response) {
        var wizard = this.down('wizardPanel');
        wizard.addData({photo: response.photoRef});
    },

    getSteps: function () {
        var items = this.getWizardPanel().items;
        items = items.filterBy(function (item) {
            return item.items.length > 0;
        });
        return items;
    }

});
