ServerResponse = {};

ServerResponse.processResponse = function (ctx,e) {
    var parse = JSON.parse(e.data);
    switch (parse.no) {
        
        case 0: //匿名登陆
            ServerResponse.AnonymousLogin(ctx,parse.data);
            break;
        
        case 1: //加入桌子
            ServerResponse.JoinTable(ctx,parse.data);
            break;
        
    }
}