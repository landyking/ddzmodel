/**
 * 发底牌
 */
ServerResponse.PublishBelowCards = function (ctx,rst) {
    console.log("Response PublishBelowCards:",rst);
    var dealerPlayer=ctx.getPlayerByPos(rst.dealerTablePos);
    var players=ctx.getAllPlayer();
    for(var i in players){
        players[i].setActionLabel("");
        if(dealerPlayer==players[i]){
            players[i].setTitle("地主");
        }else{
            players[i].setTitle("农民");
        }
    }
    ctx.tableLayer.belowCards.showCards(rst.belowCards);
    ctx.runAction(cc.sequence(cc.delayTime(3),cc.callFunc(function(){
        ctx.tableLayer.belowCards.removeFromParent();
        dealerPlayer.publishBelowCards(rst.belowCards);
        dealerPlayer.startCountdown();
    })));
};