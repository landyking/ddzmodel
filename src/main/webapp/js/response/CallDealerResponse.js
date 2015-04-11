/**
 * 叫地主
 */
ServerResponse.CallDealer = function (ctx,rst) {
    var getText=function(num){
        if(num==0){
            return "不叫";
        }else if(num==1){
            return "叫地主";
        }else if(num==2) {
            return "抢地主";
        }else if(num==3){
            return "不抢";
        }else{
            return "呵呵";
        }
    };
    var prePlayer = ctx.getPlayerByPos(rst.tablePos);
    //console.log(prePlayer);
    prePlayer.cancelCountdown();
    prePlayer.setActionLabel(getText(rst.isCall))

    if(rst.nextTablePos>=0) {
        var nextPlayer=ctx.getPlayerByPos(rst.nextTablePos);
        nextPlayer.startCountdown();
    }else{
        console.log("叫地主结束！");
    }
};