ServerResponse = {};
ServerResponse.processResponse = function (e) {
    var parse = JSON.parse(e.data);
    switch (parse.no) {
        case 10:
            ServerResponse.playCard(parse.data);
            break;
    }
}