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
    }else{
        var msg = "游戏结束：" + prePlayer.getTitle() + "获胜！";
        console.log(msg);
        ctx.runAction(cc.sequence(cc.callFunc(function(){
            cc.toast(ctx, msg);
        }),cc.delayTime(3),cc.callFunc(function(){
            cc.director.runScene(new StartMenuScene());
        })));
    }
};