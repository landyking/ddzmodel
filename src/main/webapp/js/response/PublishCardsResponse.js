/**
 * 发牌
 */
ServerResponse.PublishCards = function (ctx,rst) {
    console.log("Response PublishCards:",rst);
    ctx.waitingLayer.clearWaitingProcess();
    ctx.tableLayer.createElementOnTable();
    ctx.tableLayer.publishCards(rst.cards);
    ctx.eachPlayer(function(one){
        one.clearActionLabel();
    });
    ctx.getPlayerByPos(rst.nextTablePos).startCountdown();
};