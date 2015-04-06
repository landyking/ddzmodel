var ws = new SockJS('/ddzws');
ws.workQueue=[];
ws.onopen = function () {
    console.log('open');
};
ws.onmessage = function (e) {
    console.log('message', e.data);
    ws.workQueue.push(e);
};
ws.doWork=function(ctx){
    while(ws.workQueue.length>0) {
        var e=ws.workQueue.pop();
        if (ServerResponse) {
            ServerResponse.processResponse(ctx,e);
        }
    }
};
ws.onclose = function () {
    console.log('close');
};
