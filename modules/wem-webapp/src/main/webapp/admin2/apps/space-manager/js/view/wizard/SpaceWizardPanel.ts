module app_ui_wizard {

    export class SpaceWizardPanel extends app_ui.WizardPanel {

        private wizardHeader:app_ui.WizardHeader;

        constructor(id:string, title:string, editing:bool, data?:api_model.SpaceModel) {
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
                tbar: new SpaceWizardToolbar(headerData.isNewSpace).ext
            };
            super(panelConfig);

//            var uploader = this.ext.down('photoUploadButton');
//            uploader.on('fileuploaded', this.photoUploaded, this);
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
            var spaceStep = new SpaceStepPanel(this.data);

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
            var pathConfig:app_ui.PathConfig = {
                hidden: true
            };
            var wizardHeader = this.wizardHeader = new app_ui.WizardHeader(this.data, {}, pathConfig);

            this.validateItems.push(wizardHeader.ext);
            return wizardHeader.ext;
        }

        createIcon() {
            var me = this.ext;
            var headerData = this.resolveHeaderData();

            var formIcon = new api_app_wizard.FormIcon(headerData.iconUrl, "Click to upload icon", "rest/upload" );
            return formIcon.ext;
        }

        createActionButton() {
            return {
                xtype: 'button',
                text: 'Save',
                handler: () => {
                    new app_event.SaveSpaceEvent().fire();
                }
            };
        }

        getWizardHeader():app_ui.WizardHeader {
            return this.wizardHeader;
        }

        getData():any {
            var data = super.getData();
            var headerData = <any> this.getWizardHeader().getData();

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
