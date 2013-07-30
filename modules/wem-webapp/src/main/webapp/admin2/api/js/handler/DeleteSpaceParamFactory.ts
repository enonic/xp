module api_handler {

    export class DeleteSpaceParamFactory {

        static create(spaces:api_model.SpaceExtModel[]):api_handler.DeleteSpaceParam {

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