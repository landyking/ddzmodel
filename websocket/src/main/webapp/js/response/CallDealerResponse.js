/**
 * 叫地主
 */
ServerResponse.CallDealer = function (ctx,rst) {
    console.log("Response CallDealer:",rst);
    if(rst.tablePos == ctx.playerPos){
        ctx.tableLayer.currentPlayer.cancelCountdown();
        ctx.tableLayer.currentPlayer.setActionLabel(rst.isCall?"叫地主":"不叫");
    }
    else if((rst.tablePos+1)%3 == ctx.playerPos){
        ctx.tableLayer.leftPlayer.cancelCountdown();
        ctx.tableLayer.currentPlayer.setActionLabel(rst.isCall?"叫地主":"不叫");
    }else{
        ctx.tableLayer.rightPlayer.cancelCountdown();
        ctx.tableLayer.currentPlayer.setActionLabel(rst.isCall?"叫地主":"不叫");
    }
};