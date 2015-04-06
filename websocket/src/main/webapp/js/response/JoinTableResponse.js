/**
 * 加入桌子
 */
ServerResponse.JoinTable = function (ctx,rst) {
    console.log("Response JoinTable:",rst);
    if(rst.pid==ctx.playerId) {
        ServerRequest.RaiseHand(rst.pid);
    }
};