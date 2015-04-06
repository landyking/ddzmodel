/**
 * 匿名登陆
 */
ServerResponse.AnonymousLogin = function (ctx,rst) {
    console.log("Response AnonymousLogin:",rst);
    ctx.playerId=rst.pid;
    ServerRequest.JoinTable(-1,rst.pid)
};