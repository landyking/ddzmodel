/**
 * 加入桌子
 */
ServerResponse.JoinTable = function (ctx,rst) {
    console.log("Response JoinTable:",rst);
    if(rst.pid==ctx.playerId) {
        ctx.playerPos=rst.tablePos;
        ServerRequest.RaiseHand(rst.pid);
    }
};