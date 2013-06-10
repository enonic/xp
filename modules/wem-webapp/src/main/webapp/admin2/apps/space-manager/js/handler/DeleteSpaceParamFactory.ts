module app_handler {

    export class DeleteSpaceParamFactory {

        static create(spaces:app_model.SpaceModel[]):api_handler.DeleteSpaceParam {

            var spaceNames:string[] = [];
            for (var i = 0; i < spaces.length; i++) {
                spaceNames[i] = spaces[i].data.name;
            }

            return  {
                spaceName: spaceNames
            };
        }
    }
}