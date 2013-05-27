module admin.ui {

    export class SpaceWizardPanel extends admin.ui.WizardPanel {

        constructor(id:string, title:string, editing:bool, data?:any) {
            var headerData = this.resolveHeaderData();
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
            var wizardHeader = new admin.ui.WizardHeader(this.data, {}, pathConfig);

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
                action: 'saveSpace'
            };
        }

        getWizardHeader() {
            return this.ext.down('#wizardHeader');
        }

        getData() {
            var data = super.getData();
            var headerData = this.getWizardHeader().getData();

            return Ext.apply(data, {
                displayName: headerData.displayName,
                spaceName: headerData.name
            });
        }

        photoUploaded(photoUploadButton, response) {
            var iconRef = response.items && response.items.length > 0 && response.items[0].id;
            this.addData({iconRef: iconRef});
        }

    }

}
