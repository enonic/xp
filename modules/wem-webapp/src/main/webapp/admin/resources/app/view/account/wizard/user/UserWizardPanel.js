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

    headerTemplate: '<div class="admin-wizard-userstore">' +
                    '<label>{label}: </label>' +
                    '<span>{userstore}</span>' +
                    '<span>{qualifiedName}</span>' +
                    '</div>',

    initComponent: function () {
        var me = this;
        var isNew = this.isNewUser();

        if (me.data) {
            this.preProcessAddresses(me.data);
        }
        me.headerData = this.resolveHeaderData(me.data);

        me.tbar = {
            xtype: 'userWizardToolbar',
            isNewUser: isNew
        };
        var photoUploadButton = Ext.create({
            xclass: 'widget.photoUploadButton',
            width: 111,
            height: 111,
            style: {
                position: 'fixed',
                top: '96px'
            },
            photoUrl: me.headerData.photoUrl,
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
        });
        var displayNameField = Ext.create({
            xclass: 'widget.textfield',
            itemId: 'displayName',
            value: me.headerData.displayName,
            emptyText: 'Display Name',
            enableKeyEvents: true,
            cls: 'admin-display-name',
            dirtyCls: 'admin-display-name-dirty'
        });
        me.items = [
            {
                xtype: 'container',
                width: 121,
                padding: 5,
                items: [
                    photoUploadButton,
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
                xtype: 'container',
                columnWidth: 1,
                padding: '10 10 10 0',
                defaults: {
                    border: false
                },
                items: [
                    {
                        xtype: 'panel',
                        cls: 'admin-wizard-header-container',
                        items: [
                            displayNameField,
                            {
                                xtype: 'container',
                                itemId: 'wizardHeader',
                                styleHtmlContent: true,
                                autoHeight: true,
                                cls: 'admin-wizard-header-container',
                                tpl: new Ext.XTemplate(me.headerTemplate),
                                data: me.headerData
                            }
                        ]
                    },
                    {
                        xtype: 'wizardPanel',
                        showControls: true,
                        validateItems: [photoUploadButton, displayNameField],
                        isNew: isNew,
                        items: [
                            {
                                stepTitle: "Profile",
                                itemId: "profilePanel",
                                xtype: 'editUserFormPanel',
                                data: me.data,
                                enableToolbar: false
                            },
                            {
                                stepTitle: "User",
                                itemId: "userPanel",
                                xtype: 'editUserFormPanel',
                                data: me.data,
                                includedFields: ['name', 'email', 'password', 'repeatPassword', 'photo',
                                    'country', 'locale', 'timezone', 'globalPosition'],
                                enableToolbar: false
                            },
                            {
                                stepTitle: "Places",
                                itemId: 'placesPanel',
                                xtype: 'editUserFormPanel',
                                includedFields: ['address'],
                                data: me.data,
                                enableToolbar: false
                            },
                            {
                                stepTitle: "Memberships",
                                groups: me.headerData.userGroups,
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
            me.renderUserForms(me.data && me.data.userStore ? me.data.userStore : me.userstore);
            me.removeEmptySteps(userWizard.getWizardPanel());
            // Set active item to first one. D-02010 bug workaround
            me.getWizardPanel().getLayout().setActiveItem(0);
        });
    },


    resolveHeaderData: function (data) {

        var isNew = this.isNewUser();

        return {
            displayName: isNew ? 'Display Name' : data.displayName,
            qualifiedName: isNew ? this.userstore + '\\' : data.qualifiedName,
            label: isNew ? "New User" : "User",
            photoUrl: isNew ? 'default-image-for-user' : data.image_url,
            userGroups: isNew ? [] : data.memberships,
            edited: false
        };
    },

    preProcessAddresses: function (data) {
        // assign each address a position to be able to reflect this in diff
        if (data.profile && data.profile.addresses) {
            var i;
            for (i = 0; i < data.profile.addresses.length; i++) {
                data.profile.addresses[i].originalIndex = i;
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
        return Ext.isEmpty(this.data);
    },

    getWizardPanel: function () {
        return this.down('wizardPanel');
    },

    getData: function () {
        var wizardData = this.getWizardPanel().getData();
        if (this.data) {
            wizardData.key = this.data.key;
        }
        var displayNameField = this.down('#displayName');
        if (displayNameField) {
            var data = {displayName: displayNameField.getValue() };
            Ext.merge(wizardData, data);
        }
        return wizardData;
    },

    photoUploaded: function (photoUploadButton, response) {
        var wizard = this.down('wizardPanel'),
            photoRef = response.items && response.items.length > 0 && response.items[0].id;
        wizard.addData({imageRef: photoRef});
    },

    getSteps: function () {
        var items = this.getWizardPanel().items;
        items = items.filterBy(function (item) {
            return item.items.length > 0;
        });
        return items;
    }

});
