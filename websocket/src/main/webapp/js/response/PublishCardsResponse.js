/**
 * 发牌
 */
ServerResponse.PublishCards = function (ctx,rst) {
    console.log("Response PublishCards:",rst);
    console.log(rst.cards);
    ctx.waitingLayer.clearWaitingProcess();
    ctx.tableLayer.showPoker(rst.cards);
    ctx.getPlayerByPos(rst.nextTablePos).startCountdown();
};