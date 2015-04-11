/**
 * Created by landy on 2015/4/6.
 */
var Player = cc.Class.extend({
    titleLabel:null,
    actionLabel:null,
    handCards:null,
    countdownNum:0,


    ctor: function (parent,location) {
        var blackColor = cc.color(0, 0, 0);
        this.countdownNum=0;
        this.handCards=new HandCards(parent,location);
        this.titleLabel=new cc.LabelTTF("无名","Arial",12);
        this.titleLabel.setFontFillColor(blackColor)
        this.actionLabel = new cc.LabelTTF("","Arial",18);
        this.actionLabel.setFontFillColor(blackColor);
        if("south"==location) {
            this.titleLabel.setAnchorPoint(0,0);
            this.titleLabel.setPosition(65,5);

            this.actionLabel.setAnchorPoint(0.5,0);
            this.actionLabel.setPosition(cc.winSize.width / 2, Global.unitHeight+10);
        }else if("west"==location) {
            this.titleLabel.setAnchorPoint(0,1);
            this.titleLabel.setPosition(5,cc.winSize.height-5);

            this.actionLabel.setAnchorPoint(0,0.5);
            this.actionLabel.setPosition(Global.unitHeight,cc.winSize.height / 2);
        }else if("east"==location) {
            this.titleLabel.setAnchorPoint(1,1);
            this.titleLabel.setPosition(cc.winSize.width-5,cc.winSize.height-5);

            this.actionLabel.setAnchorPoint(1,0.5);
            this.actionLabel.setPosition(cc.winSize.width- Global.unitHeight,cc.winSize.height / 2);
        }
        parent.addChild(this.titleLabel);
        parent.addChild(this.actionLabel);

        return true;
    },
    /**
     * 设置头衔：地主|农民|无名
     * @param title unknow|dealer|farmer
     */
    setTitle: function (title) {
        this.titleLabel.setString(title);
    },
    getTitle:function() {
      return this.titleLabel.getString();
    },
    /**
     * 设置action标签：
     * 1.叫地主，不叫，抢地主，不抢
     * 2.不出
     * 3.倒计时
     * @param text
     */
    setActionLabel: function (text) {
        this.actionLabel.setString(text);
    },
    /**
     * 设置actionLabel内容为空字符串
     */
    clearActionLabel:function(){
        this.actionLabel.setString("");
    },
    hideActionLabel:function() {
      this.actionLabel.setVisible(false);
    },
    /**
     * 开始倒计时
     */
    startCountdown: function (len) {
        this.countdownNum=len||30;
        //标记，用于取消定时器。
        this._func4cancel=this._updateCountdown.bind(this);
        this.actionLabel.schedule(this._func4cancel, 1);
    },
    _updateCountdown:function(){
        this.actionLabel.setString(""+this.countdownNum);
        this.countdownNum--;
        if(this.countdownNum<0) {
            //todo 定时器到时
            this.cancelCountdown();
        }
    },
    /**
     * 取消倒计时
     */
    cancelCountdown: function () {
        this.actionLabel.setString("");
        this.actionLabel.unschedule(this._func4cancel);
        this._func4cancel=null;
    },
    /**
     * 显示操作按钮：出牌，不出，提示
     */
    showOperateButton:function(){

    },
    /**
     * 隐藏操作按钮
     */
    hideOperateButton:function(){

    },
    /**
     * 显示手牌
     */
    showHandCards:function(){

    },
    /**
     * 隐藏手牌
     */
    hideHandCards:function(){

    },
    /**
     * 发牌
     * @param cards
     */
    publishCards:function(cards){
        this.handCards.emptyCards();
        this.handCards.addCards(cards);
    },
    /**
     * 添加牌。场景：给地主发底牌
     */
    publishBelowCards:function(belowCards){
        this.handCards.addCards(belowCards);
    },
    /**
     * 出牌
     * @param cards 要出的牌
     */
    playCards:function(cards){
        this.handCards.removeCards(cards);
    },
    removeFromParent:function(){
        this.actionLabel.unschedule(this._updateCountdown);
        this.handCards.removeFromParent();
        this.titleLabel.removeFromParent();
        this.actionLabel.removeFromParent();
    }
});