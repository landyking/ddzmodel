ServerRequest = {};

ServerRequest.playCard = function (name) {
    ws.send(JSON.stringify({no: 10, data: JSON.stringify({name: name})}));
};