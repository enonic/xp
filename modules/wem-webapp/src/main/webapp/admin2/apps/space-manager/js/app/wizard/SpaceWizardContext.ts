module app_wizard {

    export class SpaceWizardContext {

        private static spaceWizardContexts:SpaceWizardContext[] = [];

        private static activeSpaceWizardContext:number = -1;

        private id:number;

        static createSpaceWizardContext():SpaceWizardContext {
            var id = spaceWizardContexts.length + 1;
            var context:SpaceWizardContext = new SpaceWizardContext(id);
            spaceWizardContexts.push(context);
            return context;
        }

        static setActiveSpaceWizardContext(value:number) {
            activeSpaceWizardContext = value;
        }

        static getActiveSpaceWizardContext():SpaceWizardContext {
            return spaceWizardContexts[activeSpaceWizardContext];
        }

        constructor(id:number) {
            this.id = id;
        }

        getId():number {
            return this.id;
        }
    }
}
