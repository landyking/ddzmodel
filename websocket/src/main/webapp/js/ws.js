var ws = new SockJS('/myHandler');
ws.onopen = function () {
    console.log('open');
};
ws.onmessage = function (e) {
    console.log('message', e.data);
    if (ServerResponse) {
        ServerResponse.processResponse(e);
    }
};
ws.onclose = function () {
    console.log('close');
};
