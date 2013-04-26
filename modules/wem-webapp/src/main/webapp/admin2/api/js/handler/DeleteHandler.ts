module admin.api.handler {

    export interface DeleteHandler
    {
        doDelete(model:any, success:() => void, failure:(error:String) => void);
    }

}
