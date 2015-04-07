/**
 * 发牌
 */
ServerResponse.PublishCards = function (ctx,rst) {
    console.log("Response PublishCards:",rst);
    console.log(rst.cards);
    ctx.waitingLayer.clearWaitingProcess();
    ctx.tableLayer.showPoker(rst.cards);
    if(rst.nextTablePos == ctx.playerPos){
        ctx.tableLayer.currentPlayer.startCountdown();
    }
    else if((rst.nextTablePos+1)%3 == ctx.playerPos){
        ctx.tableLayer.leftPlayer.startCountdown();
    }else{
        ctx.tableLayer.rightPlayer.startCountdown();
    }
};