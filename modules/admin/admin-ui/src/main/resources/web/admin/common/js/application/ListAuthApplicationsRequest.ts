module api.application {

    export class ListAuthApplicationsRequest extends api.application.ListApplicationsRequest {

        sendAndParse(): wemQ.Promise<api.application.Application[]> {

            return this.send().then((response: api.rest.JsonResponse<api.application.ApplicationListResult>) => {
                var applications = response.getResult().applications.filter((application) => !!application.authConfig);
                return api.application.Application.fromJsonArray(applications);
            });
        }
    }
}