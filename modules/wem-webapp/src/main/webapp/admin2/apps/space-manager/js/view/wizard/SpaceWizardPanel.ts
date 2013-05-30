module admin.ui {

    export class SpaceWizardPanel extends admin.ui.WizardPanel {

        private wizardHeader;

        constructor(id:string, title:string, editing:bool, data?:APP.model.SpaceModel) {
            var headerData = this.resolveHeaderData();
            this.data = data;
            var panelConfig = {
                id: id,
                editing: editing,
                title: title,
                data: data,

                itemId: 'spaceAdminWizardPanel',
                border: 0,
                autoScroll: true,
                defaults: {
                    border: false
                },
                tbar: new admin.ui.SpaceWizardToolbar(headerData.isNewSpace).ext
            };
            super(panelConfig);

            var uploader = this.ext.down('photoUploadButton');
            uploader.on('fileuploaded', this.photoUploaded, this);
        }

        private saveSpace() {
            var spaceWizardData = this.getData();
            var displayName = spaceWizardData.displayName;
            var spaceName = spaceWizardData.spaceName;
            var iconReference = spaceWizardData.iconRef;

            var spaceModel = this.data;
            var originalSpaceName = spaceModel && spaceModel.get ? spaceModel.get('name') : undefined;

            var spaceParams = {
                spaceName: originalSpaceName || spaceName,
                displayName: displayName,
                iconReference: iconReference,
                newSpaceName: (originalSpaceName !== spaceName) ? spaceName : undefined
            };

            var onUpdateSpaceSuccess = function (created, updated) {
                if (created || updated) {

                    API.notify.showFeedback('Space "' + spaceName + '" was saved');
                    components.gridPanel.refresh();
                }
            };
            Admin.lib.RemoteService.space_createOrUpdate(spaceParams, function (r) {
                if (r && r.success) {
                    onUpdateSpaceSuccess(r.created, r.updated);
                } else {
                    console.error("Error", r ? r.error : "An unexpected error occurred.");
                }
            });
        }

        resolveHeaderData() {
            var iconUrl = 'resources/images/icons/128x128/default_space.png';
            var displayNameValue = '';
            var spaceName = '';
            var data = this.data;
            if (data) {
                displayNameValue = data.get('displayName') || '';
                spaceName = data.get('name') || '';
                iconUrl = data.get('iconUrl');
            }

            return {
                'displayName': displayNameValue,
                'spaceName': spaceName,
                'isNewSpace': spaceName ? false : true,
                'iconUrl': iconUrl
            };
        }

        createSteps() {
            var spaceStep = new admin.ui.SpaceStepPanel(this.data);

            return <any[]>[
                spaceStep.ext,
                {
                    stepTitle: 'Schemas'
                },
                {
                    stepTitle: 'Modules'
                },
                {
                    stepTitle: 'Templates'
                },
                {
                    stepTitle: 'Security'
                },
                {
                    stepTitle: 'Summary'
                }
            ];
        }

        createWizardHeader() {
            var pathConfig:admin.ui.PathConfig = {
                hidden: true
            };
            var wizardHeader = this.wizardHeader = new admin.ui.WizardHeader(this.data, {}, pathConfig);

            this.validateItems.push(wizardHeader.ext);
            return wizardHeader.ext;
        }

        createIcon() {
            var me = this.ext;
            var headerData = this.resolveHeaderData();

            return {
                xtype: 'container',
                width: 110,
                height: 110,
                items: <any[]>[
                    {
                        xtype: 'photoUploadButton',
                        width: 110,
                        height: 110,
                        photoUrl: headerData.iconUrl,
                        title: "Space",
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
                            top: '5px',
                            zIndex: 1001
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
            };
        }

        createActionButton() {
            return {
                xtype: 'button',
                text: 'Save',
                handler: () => {
                    this.saveSpace();
                }
            };
        }

        getWizardHeader() {
            return this.wizardHeader;
        }

        getData():any {
            var data = super.getData();
            var headerData = this.getWizardHeader().getData();

            return this.merge(data, {
                displayName: headerData.displayName,
                spaceName: headerData.name
            });
        }

        photoUploaded(photoUploadButton, response) {
            var iconRef = response.items && response.items.length > 0 && response.items[0].id;
            this.addData({iconRef: iconRef});
        }

        private merge(obj1, obj2) {
            var obj3 = {};
            for (var attrname in obj1) {
                obj3[attrname] = obj1[attrname];
            }
            for (var attrname in obj2) {
                obj3[attrname] = obj2[attrname];
            }
            return obj3;
        }

    }

}
