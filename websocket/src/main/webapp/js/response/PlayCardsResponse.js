/**
 * 出牌
 */
ServerResponse.PlayCards = function (ctx,rst) {
    console.log("Response PlayCards:",rst);
    var prePlayer = ctx.getPlayerByPos(rst.tablePos);
    prePlayer.cancelCountdown();
    if(rst.giveUp==1){
        prePlayer.setActionLabel("不出");
    }else{
        //移除当前玩家的牌
        prePlayer.playCards(rst.cards);
        //讲打出的牌显示在桌子上
        ctx.tableLayer.playedCards.emptyCards();
        ctx.tableLayer.playedCards.addCards(rst.cards);
    }
    if(rst.nextTablePos>=0) { //-1代表没有下一位，gameover
        //设置下一位玩家的定时器
        ctx.getPlayerByPos(rst.nextTablePos).startCountdown();
    }
};