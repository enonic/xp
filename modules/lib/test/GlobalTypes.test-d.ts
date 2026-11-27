import type {
    Controller,
    CustomSelectorServiceHit,
    CustomSelectorServiceParams,
    CustomSelectorServiceResponseBody,
    MacroContext,
    Request,
    Response,
    WebSocketResponse,
} from '../core/index';

import {
    expectAssignable,
    expectNotAssignable,
    expectType,
} from 'tsd';

// ────────────────────────────────────────────────────────────────────────────
// Scenario: WebSocketResponse with typed data
// ────────────────────────────────────────────────────────────────────────────
// A type alias, since an interface is not assignable to Record<string, string>
// eslint-disable-next-line @typescript-eslint/consistent-type-definitions
type ChatData = {
    room: string;
    nickname: string;
};

expectAssignable<WebSocketResponse>({
    data: {anything: 'value'},
});

expectAssignable<WebSocketResponse<ChatData>>({
    data: {room: 'lobby', nickname: 'alice'},
});

expectNotAssignable<WebSocketResponse<ChatData>>({
    data: {room: 'lobby'},
});

expectNotAssignable<WebSocketResponse>({
    data: {count: 1},
});

// @ts-expect-error XP converts data values to strings, so only string values are allowed
type InvalidWebSocketResponse = WebSocketResponse<{count: number}>;

type ChatResponse = Response<{webSocket: WebSocketResponse<ChatData>}>;

const webSocketController = {
    get: (_request: Request): ChatResponse => ({
        webSocket: {
            data: {room: 'lobby', nickname: 'alice'},
        },
    }),
};
expectAssignable<Controller<Request, ChatResponse>>(webSocketController);

// ────────────────────────────────────────────────────────────────────────────
// Scenario: MacroContext
// ────────────────────────────────────────────────────────────────────────────
declare const request: Request;

interface YoutubeParams {
    url: string;
}

const macro = (context: MacroContext<YoutubeParams>): Response => {
    expectType<string>(context.params.url);
    return {
        body: `<iframe src="${context.params.url}"></iframe>`,
    };
};

expectAssignable<MacroContext>({
    name: 'youtube',
    body: '',
    params: {url: 'https://www.youtube.com/'},
    document: '<p>[youtube url="https://www.youtube.com/"/]</p>',
    request,
});

expectAssignable<Response>(macro({
    name: 'youtube',
    body: '',
    params: {url: 'https://www.youtube.com/'},
    document: '',
    request,
}));

// ────────────────────────────────────────────────────────────────────────────
// Scenario: CustomSelector service
// ────────────────────────────────────────────────────────────────────────────
const customSelectorService = {
    get: (req: Request<{params: CustomSelectorServiceParams}>): Response<{body: CustomSelectorServiceResponseBody}> => {
        expectType<string | undefined>(req.params.query);
        return {
            body: {
                total: 1,
                count: 1,
                hits: [{
                    id: '1',
                    displayName: 'First',
                    icon: {data: '<svg/>', type: 'image/svg+xml'},
                }],
            },
        };
    },
};
expectAssignable<Controller>(customSelectorService);

// Resolving selected options sends only ids
expectAssignable<CustomSelectorServiceParams>({
    ids: '1,2',
});

// Searching sends start and count, and query once the user has typed something
expectAssignable<CustomSelectorServiceParams>({
    start: '0',
    count: '10',
});

// Params from the input config can be added with an intersection
type CountrySelectorParams = CustomSelectorServiceParams & {
    region?: string;
};
const countrySelectorService = {
    get: (req: Request<{params: CountrySelectorParams}>): Response<{body: CustomSelectorServiceResponseBody}> => {
        expectType<string | undefined>(req.params.region);
        return {
            body: {total: 0, count: 0, hits: []},
        };
    },
};
expectAssignable<Controller<Request<{params: CountrySelectorParams}>>>(countrySelectorService);

expectAssignable<CustomSelectorServiceHit>({
    id: '2',
    displayName: 'Second',
    description: 'With an icon URL',
    iconUrl: 'https://example.com/icon.png',
});

expectNotAssignable<CustomSelectorServiceHit>({
    id: '3',
});

expectNotAssignable<CustomSelectorServiceResponseBody>({
    total: 1,
    count: 1,
    hits: [{id: '1'}],
});
